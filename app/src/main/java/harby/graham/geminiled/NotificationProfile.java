package harby.graham.geminiled;

import android.support.annotation.NonNull;

/**
 * To do.
 */

class NotificationProfile implements Comparable<NotificationProfile>{

    private String packName;
    private Colour colour;

    NotificationProfile(String packName, Colour colour) {
        this.packName = packName;
        this.colour = colour;
    }

    NotificationProfile(String packName, int colour){
        this.packName = packName;
        this.colour = new Colour(colour);

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
}
