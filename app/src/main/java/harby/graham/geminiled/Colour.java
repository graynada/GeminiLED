package harby.graham.geminiled;

/**
 * The Colour class gives a representation of one 8 colours possible in the 3 bit colour range.
 */

class Colour {

    /**Red bit of the Colour */
    int r;
    /**Green bit of the Colour */
    int g;
    /**Blue bit of the Colour */
    int b;

    /** The default value of the z argument for the Planet Computers openLed(led, r, g, b, z) method*/
    static final int Z = 0;
    /** Black colour value 0*/
    static final int BLACK = 0;
    /** Blue colour value 1*/
    static final int BLUE = 1;
    /** Green colour value 2*/
    static final int GREEN = 2;
    /** Cyan colour value 3*/
    static final int CYAN = 3;
    /** Red colour value 4*/
    static final int RED = 4;
    /** Magenta colour value 5*/
    static final int MAGENTA = 5;
    /** Yellow colour value 6*/
    static final int YELLOW = 6;
    /** White colour value 7*/
    static final int WHITE = 7;

    /**
     * Creates a 3 bit colour to the supplied value in the range 0-7
     *
     * @param i decimal of 3 bit colour 0-7
     */
    Colour(int i){
        switch (i) {
            case BLACK:
                r = 0;
                g = 0;
                b = 0;
                break;
            case BLUE:
                r = 0;
                g = 0;
                b = 1;
                break;
            case GREEN:
                r = 0;
                g = 1;
                b = 0;
                break;
            case CYAN:
                r = 0;
                g = 1;
                b = 1;
                break;
            case RED:
                r = 1;
                g = 0;
                b = 0;
                break;
            case MAGENTA:
                r = 1;
                g = 0;
                b = 1;
                break;
            case YELLOW:
                r = 1;
                g = 1;
                b = 0;
                break;
            case WHITE:
                r = 1;
                g = 1;
                b = 1;
                break;
        }
    }

    /**
     * Returns the decimal value of the Colour in the range 0-7
     *
     * @return Decimal of the Colour
     */
    int getInt(){
        return (r * 4) + (g * 2) + b;
    }

}
