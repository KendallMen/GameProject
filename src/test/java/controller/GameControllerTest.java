package controller;

import model.Game;
import model.GameState;
import model.PieceColor;
import model.Player;
import org.junit.jupiter.api.Test;
import repository.GameRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class GameControllerTest {

    private static class InMemoryGameRepository implements GameRepository {
        private final Map<String, Game> store = new HashMap<>();

        @Override
        public void guardar(String id, Game partida) {
            store.put(id, partida);
        }

        @Override
        public Game buscarPorId(String id) {
            return store.get(id);
        }

        @Override
        public Game[] obtenerTodas() {
            return store.values().toArray(new Game[0]);
        }

        @Override
        public void eliminar(String id) {
            store.remove(id);
        }
    }

    @Test
    void startNewGameRejectsNullPlayers() {
        GameController controller = new GameController(new InMemoryGameRepository());

        assertEquals(ControllerResult.INVALID_INPUT, controller.startNewGame(4, null, new Player("B")));
        assertEquals(ControllerResult.INVALID_INPUT, controller.startNewGame(4, new Player("A"), null));
    }

    @Test
    void readStateWithoutGameReturnsNoActiveGame() {
        GameController controller = new GameController(new InMemoryGameRepository());
        GameState state = new GameState();

        assertEquals(ControllerResult.NO_ACTIVE_GAME, controller.readState(state));
        assertNull(state.getBoard());
        assertEquals(PieceColor.EMPTY, state.getTurn());
        assertFalse(state.isGameOver());
        assertEquals(0, state.getBlackCount());
        assertEquals(0, state.getWhiteCount());
        assertEquals("", state.getResultText());
    }

    @Test
    void readStateAfterStartReturnsGameData() {
        GameController controller = new GameController(new InMemoryGameRepository());
        Player p1 = new Player("A");
        Player p2 = new Player("B");
        controller.startNewGame(4, p1, p2);

        GameState state = new GameState();
        assertEquals(ControllerResult.SUCCESS, controller.readState(state));
        assertNotNull(state.getBoard());
        assertEquals(4, state.getBoard().getTamano());
        assertEquals(PieceColor.BLACK, state.getTurn());
    }

    @Test
    void moveWithoutGameReturnsNoActiveGame() {
        GameController controller = new GameController(new InMemoryGameRepository());
        assertEquals(ControllerResult.NO_ACTIVE_GAME, controller.move(0, 0));
    }

    @Test
    void moveOnOccupiedCellIsInvalid() {
        GameController controller = new GameController(new InMemoryGameRepository());
        controller.startNewGame(4, new Player("A"), new Player("B"));

        assertEquals(ControllerResult.INVALID_MOVE, controller.move(1, 1));
    }

    @Test
    void passTurnNotAllowedWhenMovesExist() {
        GameController controller = new GameController(new InMemoryGameRepository());
        controller.startNewGame(4, new Player("A"), new Player("B"));

        assertEquals(ControllerResult.PASS_NOT_ALLOWED, controller.passTurn());
    }

    @Test
    void saveGameValidatesIdAndStoresGame() {
        InMemoryGameRepository repo = new InMemoryGameRepository();
        GameController controller = new GameController(repo);
        controller.startNewGame(4, new Player("A"), new Player("B"));

        assertEquals(ControllerResult.INVALID_INPUT, controller.saveGame(" "));
        assertEquals(ControllerResult.SUCCESS, controller.saveGame("test"));
        assertNotNull(repo.buscarPorId("test"));
    }

    @Test
    void loadGameHandlesNotFound() {
        InMemoryGameRepository repo = new InMemoryGameRepository();
        GameController controller = new GameController(repo);

        assertEquals(ControllerResult.LOAD_NOT_FOUND, controller.loadGame("missing"));
    }
}

