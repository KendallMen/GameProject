package model;

/**
 * Representa una ficha (color).
 */
public class Disc {
    private final char color; // 'B' negra, 'W' blanca

    /**
     * Crea una ficha con el color indicado.
     *
     * @param color 'B' o 'W'
     */
    public Disc(char color) {
        this.color = color;
    }


    public char getColor() {
        return color;
    }


    public static char colorOponente(char color) {
        if (color == 'B') {
            return 'W';
        }
        return 'B';
    }
}
