package harby.graham.geminiled;

import android.util.Log;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by graham on 27/06/18.
 */

class GeminiLED {

    static final String TAG = "GeminiLED";
    Map <Integer, NotificationProfile> ledMap;
    static int ledOnTime = 2000;
    static int ledOffTime = 500;

    GeminiLED(){
        ledMap = new HashMap<>();
        ledMap.put(5, new NotificationProfile("viber", new Colour(Colour.MAGENTA)));
        ledMap.put(4, new NotificationProfile("whatsapp", new Colour(Colour.GREEN)));
        ledMap.put(3, new NotificationProfile("k9", new Colour(Colour.RED)));
        ledMap.put(2, new NotificationProfile("handcent", new Colour(Colour.BLUE)));
        ledMap.put(1, new NotificationProfile("dialer", new Colour(Colour.CYAN)));
        Log.i(TAG, "GeminiLED initialised");
    }

    Integer getKey(String s){
        Integer key = 256;
            for(Integer i: ledMap.keySet()){
                if(s.contains(ledMap.get(i).getPackName())) {
                    key = i;
                }
            }
        return key;
    }

}
