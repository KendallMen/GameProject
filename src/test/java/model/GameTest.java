package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    @Test
    void constructorRejectsNullPlayers() {
        Player p = new Player("A");
        assertThrows(IllegalArgumentException.class, () -> new Game(4, null, p));
        assertThrows(IllegalArgumentException.class, () -> new Game(4, p, null));
    }

    @Test
    void initialStateHasBlackTurnAndTwoPiecesEach() {
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertEquals(PieceColor.BLACK, game.getTurno());
        assertEquals(2, game.contarNegras());
        assertEquals(2, game.contarBlancas());
    }

    @Test
    void jugarValidMoveChangesTurnAndFlips() {
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertTrue(game.jugar(0, 1));
        assertEquals(PieceColor.WHITE, game.getTurno());
        assertEquals(PieceColor.BLACK, game.getTablero().ficha(1, 1));
        assertEquals(4, game.contarNegras());
        assertEquals(1, game.contarBlancas());
    }

    @Test
    void jugarInvalidMoveKeepsTurn() {
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertFalse(game.jugar(0, 0));
        assertEquals(PieceColor.BLACK, game.getTurno());
    }

    @Test
    void juegoTerminadoWhenBoardIsFull() {
        Game game = new Game(4, new Player("A"), new Player("B"));
        PieceColor[][] full = new PieceColor[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                full[r][c] = PieceColor.BLACK;
            }
        }
        game.getTablero().cargarEstado(full);

        assertTrue(game.juegoTerminado());
    }

    @Test
    void resultadoFinalDetectsTie() {
        Game game = new Game(4, new Player("A"), new Player("B"));
        PieceColor[][] tie = new PieceColor[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                tie[r][c] = (r + c) % 2 == 0 ? PieceColor.BLACK : PieceColor.WHITE;
            }
        }
        game.getTablero().cargarEstado(tie);

        assertEquals("EMPATE", game.resultadoFinal());
    }
}

