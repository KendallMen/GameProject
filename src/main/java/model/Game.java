package model;

public class Game {

    private final Board tablero;
    private final Player jugadorNegro;
    private final Player jugadorBlanco;

    private PieceColor turno;

    public Game(int n, Player jugadorNegro, Player jugadorBlanco) {
        if (jugadorNegro == null || jugadorBlanco == null) {
            throw new IllegalArgumentException("Los jugadores no pueden ser null");
        }

        this.tablero = new Board(n);
        this.jugadorNegro = jugadorNegro;
        this.jugadorBlanco = jugadorBlanco;

        this.turno = PieceColor.BLACK; // siempre inicia negro
    }

    public Board getTablero() {
        return tablero; }
    public Player getJugadorNegro() {
        return jugadorNegro; }
    public Player getJugadorBlanco() {
        return jugadorBlanco; }
    public PieceColor getTurno() {
        return turno; }

    public void cambiarTurno() {
        turno = turno.opposite();
    }

    public boolean jugar(int fila, int col) {
        boolean exito = tablero.ponerFicha(fila, col, turno);

        if (exito) {
            cambiarTurno();

            if (!tablero.hayMovimientos(turno) && !juegoTerminado()) {
                cambiarTurno();
            }
        }

        return exito;
    }

    public void pasarTurno() {
        cambiarTurno();
        if (!tablero.hayMovimientos(turno) && !juegoTerminado()) {
            cambiarTurno();
        }
    }

    public boolean juegoTerminado() {
        if (tablero.tableroLleno()) return true;
        if (!tablero.hayMovimientos(PieceColor.BLACK) && !tablero.hayMovimientos(PieceColor.WHITE)) return true;
        return false;
    }

    public int contarNegras() {
        return tablero.contarFichas(PieceColor.BLACK); }
    public int contarBlancas() {
        return tablero.contarFichas(PieceColor.WHITE); }

    public String resultadoFinal() {
        int negras = contarNegras();
        int blancas = contarBlancas();

        if (negras > blancas) return "GANA NEGRO";
        if (blancas > negras) return "GANA BLANCO";
        return "EMPATE";
    }
}

