package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

    @Test
    void constructorRejectsOddOrSmall() {
        assertThrows(IllegalArgumentException.class, () -> new Board(3));
        assertThrows(IllegalArgumentException.class, () -> new Board(5));
    }

    @Test
    void initialSetupPlacesCenterPieces() {
        Board board = new Board(4);

        assertEquals(PieceColor.WHITE, board.ficha(1, 1));
        assertEquals(PieceColor.BLACK, board.ficha(1, 2));
        assertEquals(PieceColor.BLACK, board.ficha(2, 1));
        assertEquals(PieceColor.WHITE, board.ficha(2, 2));
    }

    @Test
    void movValidoDetectsValidAndInvalidMoves() {
        Board board = new Board(4);

        assertTrue(board.movValido(0, 1, PieceColor.BLACK));
        assertFalse(board.movValido(0, 1, PieceColor.WHITE));
        assertFalse(board.movValido(0, 0, PieceColor.BLACK));
    }

    @Test
    void ponerFichaFlipsOpponentPieces() {
        Board board = new Board(4);

        assertTrue(board.ponerFicha(0, 1, PieceColor.BLACK));
        assertEquals(PieceColor.BLACK, board.ficha(0, 1));
        assertEquals(PieceColor.BLACK, board.ficha(1, 1));

        assertEquals(4, board.contarFichas(PieceColor.BLACK));
        assertEquals(1, board.contarFichas(PieceColor.WHITE));
    }

    @Test
    void copiarEstadoReturnsIndependentSnapshot() {
        Board board = new Board(4);
        PieceColor[][] snapshot = board.copiarEstado();

        snapshot[0][0] = PieceColor.BLACK;
        assertEquals(PieceColor.EMPTY, board.ficha(0, 0));
    }

    @Test
    void cargarEstadoValidatesDimensions() {
        Board board = new Board(4);
        PieceColor[][] wrong = new PieceColor[3][3];

        assertThrows(IllegalArgumentException.class, () -> board.cargarEstado(wrong));
    }

    @Test
    void tableroLlenoDetectsFullBoard() {
        Board board = new Board(4);
        PieceColor[][] full = new PieceColor[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                full[r][c] = PieceColor.BLACK;
            }
        }
        board.cargarEstado(full);

        assertTrue(board.tableroLleno());
        assertFalse(board.hayMovimientos(PieceColor.WHITE));
    }
}

