package harby.graham.geminiled;

import android.app.NotificationManager;
import android.content.Context;
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
    Set<Integer> activeLEDList;

    @Override
    public void onCreate() {

        super.onCreate();
        context = getApplicationContext();

        Log.i(GeminiLED.TAG, "Gemini Notification Listener service started");
        reflect(context);
        geminiLED = new GeminiLED();
        activeLEDList = new HashSet<>();
    }

    @Override
    public void onNotificationPosted(StatusBarNotification sbn) {

        String pack = sbn.getPackageName();
        String title = sbn.getNotification().extras.getString("android.title");

        String text = "Notification from: " + pack + " title: " + title;
        Log.i(GeminiLED.TAG, text);

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
    }

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
                int i = geminiLED.getKey(sbn.getPackageName());
                if (i < GeminiLED.KEY) {
                    activeLEDList.add(i);
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