package harby.graham.geminiled;

import android.app.NotificationManager;
import android.content.Context;
import android.os.SystemClock;
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
    static GeminiLED geminiLED;

    Method method;
    boolean active = false;
    Set<Integer> activeLEDList;
    Thread blinkLED;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

        Log.i(GeminiLED.TAG, "Gemini Notification Listener service started");
        reflect(context);
        geminiLED = new GeminiLED();
        activeLEDList = new HashSet<>();

        blinkLED = new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i(GeminiLED.TAG, "blinkLED started");
                while(active){
                    active = loadActiveLEDList();
                    lightLEDs();
//                    try {
                        SystemClock.sleep(GeminiLED.ledOnTime);
//                    }
//                    catch (InterruptedException e) {
//                        System.out.println("blinkLED ledOnTime interrupt " + e);
//                    }
                    blankLEDs();
//                    try {
                        SystemClock.sleep(GeminiLED.ledOffTime);
//                    }
//                    catch (InterruptedException e) {
//                        System.out.println("blinkLED ledOffTime interrupt " + e);
//                    }
                }
            }
        });

        active = loadActiveLEDList();
        if(active){
            blinkLED.start();
        }
        else{
            Log.i(GeminiLED.TAG, "active LEDs not detected");
        }
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title");

        String text = "Notification from: " + pack + " title: " + title;
        Log.i(GeminiLED.TAG, text);

//        if(lightLED(pack) && !active){
//            active = true;
//            blinkLED.start();
//        }
        loadActiveLEDList();
    }

    @Override
    public void onNotificationRemoved(StatusBarNotification sbn) {
        super.onNotificationRemoved(sbn);

        String pack = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title");

        String text = "Notification removed: " + pack + " title: " + title;
        Log.i(GeminiLED.TAG, text);
        loadActiveLEDList();
//        if(!loadActiveLEDList() && blinkLED.isAlive()){
//            blinkLED.interrupt();
//        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        blankLEDs();
//        if(blinkLED.isAlive()){
//            blinkLED.interrupt();
//        }
    }

    boolean loadActiveLEDList(){
        boolean activeLEDs = false;
        activeLEDList.clear();
        StatusBarNotification[] sbns = getActiveNotifications();
        if(!(sbns==null)) {
            for (StatusBarNotification sbn : sbns) {
                int i = geminiLED.getKey(sbn.getPackageName());
                if (i < 255) {
                    activeLEDList.add(i);
                    activeLEDs = true;
                }
            }
        }
        lightLEDs();
        return activeLEDs;
    }

    boolean lightLED(String pack){
        boolean lit = false;
        int led = geminiLED.getKey(pack);
        if(led < 255){
            openLed(led, geminiLED.ledMap.get(led).getColour());
            lit = true;
        }
        return lit;
    }

    void lightLEDs(){
        blankLEDs();
        if(activeLEDList.size() > 0) {
            for (Integer i : activeLEDList) {
                openLed(i, geminiLED.ledMap.get(i).getColour());
            }
        }
//        else{
//            active = false;
//            blankLEDs();
//        }
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
            Log.i(GeminiLED.TAG, "reflection executed");
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

}