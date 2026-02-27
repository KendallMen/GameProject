package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameStateTest {

    @Test
    void setAndGetBoard() {
        GameState state = new GameState();
        Board board = new Board(4);
        state.setBoard(board);

        assertSame(board, state.getBoard());
    }

    @Test
    void setAndGetPlayers() {
        GameState state = new GameState();
        Player black = new Player("Black");
        Player white = new Player("White");
        state.setBlack(black);
        state.setWhite(white);

        assertSame(black, state.getBlack());
        assertSame(white, state.getWhite());
    }

    @Test
    void setAndGetTurn() {
        GameState state = new GameState();
        state.setTurn(PieceColor.WHITE);

        assertEquals(PieceColor.WHITE, state.getTurn());
    }

    @Test
    void setAndGetCounts() {
        GameState state = new GameState();
        state.setBlackCount(5);
        state.setWhiteCount(3);

        assertEquals(5, state.getBlackCount());
        assertEquals(3, state.getWhiteCount());
    }

    @Test
    void setAndGetGameOverAndResult() {
        GameState state = new GameState();
        state.setGameOver(true);
        state.setResultText("GANA NEGRO");

        assertTrue(state.isGameOver());
        assertEquals("GANA NEGRO", state.getResultText());
    }
}

