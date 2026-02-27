package model;

public enum PieceColor {
    BLACK,
    WHITE,
    EMPTY;

    public PieceColor opposite() {
        return switch (this) {
            case BLACK -> WHITE;
            case WHITE -> BLACK;
            default -> EMPTY;
        };
    }
}
