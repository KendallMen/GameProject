package model;

public enum PieceColor {
    BLACK,
    WHITE,
    EMPTY;

    /**
     * Devuelve el color opuesto (EMPTY se mantiene igual).
     * @return color opuesto o EMPTY
     */
    public PieceColor opposite() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            default -> EMPTY;
        };
    }
}
