package harby.graham.geminiled;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.service.notification.NotificationListenerService;
import android.service.notification.StatusBarNotification;
import android.util.Log;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Set;

/**
 * To do.
 */

public class GeminiNotificationListener extends NotificationListenerService {

    Context context;
    NotificationManager mNotificationManager;
    NotificationReceiver notificationReceiver;
    static final String FILTER = "harby.graham.geminiled.GNL_SERVICE";
    static GeminiLED geminiLED;

    Method method;
    Set<Integer> activeLEDList;

    static final String PREF_FILE = "harby.graham.geminiled";
    static String defaultPackage = PREF_FILE;
    int defaultColour = Colour.WHITE;
    SharedPreferences savedLedMap;

    String[] dataKeys = {"one", "two", "three", "four", "five"};
    String[] dataTypes = {"package", "colour"};
    String kbLEDenabled = "keyboardLEDon";
    String coverLEDs = "coverLEDsOn";

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

        Log.i(GeminiLED.TAG, "Gemini Notification Listener service started");

        geminiLED = new GeminiLED();
        loadData();
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FILTER);
        registerReceiver(notificationReceiver, filter);
        reflect(context);
        activeLEDList = new HashSet<>();
        loadActiveLEDList();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        //For future version
//        String pack = sbn.getPackageName();
//        String titleAndText = getTitleAndText(sbn);
//
//        String text = "Notification from: " + pack + titleAndText;
        loadActiveLEDList();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        //For future version
//        String pack = sbn.getPackageName();
//        String titleAndText = getTitleAndText(sbn);

//        String text = "Notification removed: " + pack + titleAndText;
//        Log.i(GeminiLED.TAG, text);
        loadActiveLEDList();
    }

    //For future version
//    String getTitleAndText(StatusBarNotification sbn){
//        Bundle extras = sbn.getNotification().extras;
//        CharSequence title = "";
//        CharSequence text = "";
//        if (extras != null) {
//            title = extras.getCharSequence("android.title");
//            text = extras.getCharSequence("android.text");
//        }
//        return " Title: " + title + " Text: " + text;
//    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        blankLEDs();
    }

    void loadActiveLEDList(){
        activeLEDList.clear();
        StatusBarNotification[] sbns = getActiveNotifications();
        if(!(sbns==null)) {
            for (StatusBarNotification sbn : sbns) {
                for(int i: geminiLED.ledMap.keySet()){
                    if(geminiLED.ledMap.get(i).getPackName().equals(sbn.getPackageName())){
                        activeLEDList.add(i);
                    }
                }
            }
        }
        lightLEDs();
    }

    void lightLEDs(){
        if(geminiLED.coverLEDsOn) {
            for (Integer i : geminiLED.ledMap.keySet()) {
                if (activeLEDList.contains(i)) {
                    openLed(i, geminiLED.ledMap.get(i).getColour());
                } else {
                    openLed(i, new Colour(Colour.BLACK));
                }
            }
        }
        else{
            blankLEDs();
        }
        if(activeLEDList.size()>0 && geminiLED.keyboardLEDon){
            openLed(7, new Colour(Colour.GREEN));
        }
        else {
            openLed(7, new Colour(Colour.BLACK));
        }
    }

    void blankLEDs(){
        for(Integer i: geminiLED.ledMap.keySet()){
            openLed(i, new Colour(Colour.BLACK));
        }
    }

    public void reflect(Context context) {
        mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        try {
            method = mNotificationManager.getClass().getMethod("openLed", int.class, int.class, int.class, int.class, int.class);
        } catch (SecurityException e) {
            Log.e(GeminiLED.TAG, "Security prevents reflection");
        } catch (NoSuchMethodException e) {
            Log.e(GeminiLED.TAG, "No method");
        }

    }

    void openLed(int led, Colour colour){
        try {
            method.invoke(mNotificationManager, led, colour.r, colour.g, colour.b, Colour.Z);
        } catch (IllegalAccessException e) {
            Log.e(GeminiLED.TAG, "IllegalAccessException");
        } catch (InvocationTargetException e) {
            Log.e(GeminiLED.TAG, "InvocationTargetException");
        }
    }

    class NotificationReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String command = intent.getStringExtra(MainActivity.COMMAND);
            switch (command){
                case MainActivity.UPDATE:
                    writeData();
                    loadActiveLEDList();
                    break;
            }
        }
    }

    void loadData(){
        savedLedMap = getApplicationContext().getSharedPreferences(PREF_FILE, MODE_PRIVATE);
        for(int i = 0; i < dataKeys.length; ++i){
            String packKey = dataKeys[i] + dataTypes[0];
            String pack = defaultPackage;
            String colourKey = dataKeys[i] + dataTypes[1];
            int colour = defaultColour;
            if (savedLedMap.contains(packKey)) {
                pack = savedLedMap.getString(packKey, defaultPackage);
            }
            if (savedLedMap.contains(colourKey)) {
                colour = savedLedMap.getInt(colourKey, defaultColour);
            }
            geminiLED.ledMap.put(i + 1, new NotificationProfile(pack, colour));
        }
        if(savedLedMap.contains(kbLEDenabled)) {
            geminiLED.keyboardLEDon = savedLedMap.getBoolean(kbLEDenabled, false);
        }
        if(savedLedMap.contains(coverLEDs)){
            geminiLED.coverLEDsOn = savedLedMap.getBoolean(coverLEDs, true);
        }
    }

    void writeData(){
        SharedPreferences.Editor editor = savedLedMap.edit();
        for(int i: geminiLED.ledMap.keySet()){
            String packKey = dataKeys[i - 1] + dataTypes[0];
            String colourKey = dataKeys[i - 1] + dataTypes[1];
            editor.putString(packKey, geminiLED.ledMap.get(i).getPackName());
            editor.putInt(colourKey, geminiLED.ledMap.get(i).getColour().getInt());
        }
        editor.putBoolean(kbLEDenabled, geminiLED.keyboardLEDon);
        editor.putBoolean(coverLEDs, geminiLED.coverLEDsOn);
        editor.apply();
    }



}