package model;

/**
 * Representa el tablero N x N y operaciones propias del tablero.
 */
public class Board {

    private PieceColor[][] tablero;
    private int tamano;

    private final int[] dfila = {-1,-1,-1, 0, 0, 1, 1, 1};
    private final int[] dcol  = {-1, 0, 1,-1, 1,-1, 0, 1};

    public Board(int n) {
        if (n < 4 || n % 2 != 0) {
            throw new IllegalArgumentException("El tamaño debe ser par y >= 4");
        }

        tamano = n;
        tablero = new PieceColor[tamano][tamano];

        inicializarVacio();
        colocarInicial();
    }

    public int getTamano() {
        return tamano;
    }

    public PieceColor ficha(int fila, int col) {
        return tablero[fila][col];
    }

    private void inicializarVacio() {
        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                tablero[f][c] = PieceColor.EMPTY;
            }
        }
    }

    private void colocarInicial() {

        int mid1 = (tamano / 2) - 1;
        int mid2 = (tamano / 2);

        tablero[mid1][mid1] = PieceColor.WHITE;
        tablero[mid1][mid2] = PieceColor.BLACK;
        tablero[mid2][mid1] = PieceColor.BLACK;
        tablero[mid2][mid2] = PieceColor.WHITE;
    }

    private boolean posicionValida(int fila, int col) {
        return fila >= 0 && fila < tamano && col >= 0 && col < tamano;
    }

    public boolean ponerFicha(int fila, int col, PieceColor color) {

        if (!posicionValida(fila, col)) return false;
        if (tablero[fila][col] != PieceColor.EMPTY) return false;
        if (!movValido(fila, col, color)) return false;

        tablero[fila][col] = color;
        voltearFichas(fila, col, color);

        return true;
    }

    public boolean movValido(int fila, int col, PieceColor color) {

        if (!posicionValida(fila, col)) return false;
        if (tablero[fila][col] != PieceColor.EMPTY) return false;

        PieceColor oponente = color.opposite();

        for (int d = 0; d < 8; d++) {

            int f = fila + dfila[d];
            int c = col + dcol[d];

            if (posicionValida(f, c) && tablero[f][c] == oponente) {

                while (posicionValida(f, c) && tablero[f][c] == oponente) {
                    f += dfila[d];
                    c += dcol[d];
                }

                if (posicionValida(f, c) && tablero[f][c] == color) {
                    return true;
                }
            }
        }

        return false;
    }

    private void voltearFichas(int fila, int col, PieceColor color) {

        PieceColor oponente = color.opposite();
        int max = tamano * tamano;

        for (int d = 0; d < 8; d++) {

            int f = fila + dfila[d];
            int c = col + dcol[d];

            if (posicionValida(f, c) && tablero[f][c] == oponente) {

                int[] filas = new int[max];
                int[] cols  = new int[max];
                int count = 0;

                while (posicionValida(f, c) && tablero[f][c] == oponente) {
                    filas[count] = f;
                    cols[count] = c;
                    count++;
                    f += dfila[d];
                    c += dcol[d];
                }

                if (posicionValida(f, c) && tablero[f][c] == color) {
                    for (int i = 0; i < count; i++) {
                        tablero[filas[i]][cols[i]] = color;
                    }
                }
            }
        }
    }

    public int contarFichas(PieceColor color) {
        int total = 0;

        for (int f = 0; f < tamano; f++) {
            for (int c = 0; c < tamano; c++) {
                if (tablero[f][c] == color) total++;
            }
        }

        return total;
    }

    public boolean tableroLleno() {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                if (tablero[f][c] == PieceColor.EMPTY) return false;

        return true;
    }

    public boolean hayMovimientos(PieceColor color) {
        for (int f = 0; f < tamano; f++)
            for (int c = 0; c < tamano; c++)
                if (tablero[f][c] == PieceColor.EMPTY && movValido(f,c,color))
                    return true;

        return false;
    }

    // =========================
    //  NUEVO: soporte JSON (snapshot)
    // =========================

    /** Devuelve una copia del tablero para persistencia (sin aliasing). */
    public PieceColor[][] copiarEstado() {
        PieceColor[][] copy = new PieceColor[tamano][tamano];
        for (int f = 0; f < tamano; f++) {
            System.arraycopy(tablero[f], 0, copy[f], 0, tamano);
        }
        return copy;
    }

    /** Carga un estado persistido (si es inválido, lanza excepción). */
    public void cargarEstado(PieceColor[][] estado) {
        if (estado == null || estado.length != tamano || estado[0].length != tamano) {
            throw new IllegalArgumentException("Estado inválido para el tamaño del tablero");
        }
        for (int f = 0; f < tamano; f++) {
            if (estado[f] == null || estado[f].length != tamano) {
                throw new IllegalArgumentException("Fila inválida en el estado");
            }
            System.arraycopy(estado[f], 0, tablero[f], 0, tamano);
        }
    }
}
