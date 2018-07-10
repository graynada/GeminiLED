package harby.graham.geminiled;

import java.util.HashMap;
import java.util.Map;

/**
 * To do.
 */

class GeminiLED {

    static final String TAG = "GeminiLED";
    Map <Integer, NotificationProfile> ledMap;
    static final int KEY = 256;

    GeminiLED(){
        ledMap = new HashMap<>();
    }

    Integer getKey(String s){
        Integer key = KEY;
            for(Integer i: ledMap.keySet()){
                if(s.contains(ledMap.get(i).getPackName())) {
                    key = i;
                }
            }
        return key;
    }


}
