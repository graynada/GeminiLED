package harby.graham.geminiled;

import android.service.notification.StatusBarNotification;
import android.support.annotation.NonNull;

import java.util.LinkedHashSet;

/**
 * To do.
 */

class NotificationProfile implements Comparable<NotificationProfile>{

    private String packName;
    private Colour colour;

    private boolean advanced;
    private LinkedHashSet<Element> elements;

    NotificationProfile(String packName, Colour colour) {
        this.packName = packName;
        this.colour = colour;
        advanced = false;
    }

    NotificationProfile(String packName, int colour){
        this(packName, new Colour(colour));
//        this.packName = packName;
//        this.colour = new Colour(colour);

    }

    boolean notify(StatusBarNotification sbn){
        boolean result = false;

        return result;
    }

    String getPackName() {
        return packName;
    }

    Colour getColour() {
        return colour;
    }

    void setColour(Colour colour) {
        this.colour = colour;
    }

    @Override
    public int compareTo(@NonNull NotificationProfile np) {
        return np.packName.compareTo(this.packName);
    }

    class Element {

        String testString;
        boolean inPackageName;
        boolean inTitle;
        boolean inText;
        boolean exactMatch;
    }

}
