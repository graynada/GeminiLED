package harby.graham.geminiled;

import android.support.annotation.NonNull;

/**
 * To do.
 */

class NotificationProfile implements Comparable<NotificationProfile>{

    private String packName;
    private Colour colour;

    public NotificationProfile(String packName, Colour colour) {
        this.packName = packName;
        this.colour = colour;
    }

    public String getPackName() {
        return packName;
    }

    public void setPackName(String packName) {
        this.packName = packName;
    }

    public Colour getColour() {
        return colour;
    }

    public void setColour(Colour colour) {
        this.colour = colour;
    }

    @Override
    public int compareTo(@NonNull NotificationProfile np) {
        return np.packName.compareTo(this.packName);
    }
}
