package harby.graham.geminiled;

import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

        Log.i(GeminiLED.TAG, "Gemini Notification Listener service started");

        geminiLED = new GeminiLED();
        notificationReceiver = new NotificationReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(FILTER);
        registerReceiver(notificationReceiver, filter);
        reflect(context);
        activeLEDList = new HashSet<>();
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
//                int i = geminiLED.getKey(sbn.getPackageName());
//                if (i < GeminiLED.KEY) {
//                    activeLEDList.add(i);
//                }
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
        for(Integer i: geminiLED.ledMap.keySet()){
            if(activeLEDList.contains(i)){
                openLed(i, geminiLED.ledMap.get(i).getColour());
            }
            else{
                openLed(i, new Colour(Colour.BLACK));
            }
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
                    loadActiveLEDList();
                    break;
            }
        }
    }

}