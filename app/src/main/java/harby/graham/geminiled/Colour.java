package harby.graham.geminiled;

/**
 * To do.
 */

class Colour {

    int r, g, b;

    static final int Z = 0;
    static final int BLACK = 0;
    static final int BLUE = 1;
    static final int GREEN = 2;
    static final int CYAN = 3;
    static final int RED = 4;
    static final int MAGENTA = 5;
    static final int YELLOW = 6;
    static final int WHITE = 7;

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

}
