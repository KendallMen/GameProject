package controller;

import model.Game;
import model.PieceColor;
import model.Player;
import org.junit.jupiter.api.Test;
import repository.PlayerRepository;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class PlayerControllerTest {

    private static class InMemoryPlayerRepository implements PlayerRepository {
        private final Map<String, Player> store = new HashMap<>();

        @Override
        public void agregar(Player jugador) {
            store.put(jugador.getNombre().toLowerCase(), jugador);
        }

        @Override
        public Player buscarPorNombre(String nombre) {
            if (nombre == null) return null;
            return store.get(nombre.toLowerCase());
        }

        @Override
        public Player[] obtenerTodos() {
            return store.values().toArray(new Player[0]);
        }

        @Override
        public boolean existe(String nombre) {
            if (nombre == null) return false;
            return store.containsKey(nombre.toLowerCase());
        }

        @Override
        public void actualizar(Player jugador) {
            store.put(jugador.getNombre().toLowerCase(), jugador);
        }

        @Override
        public void eliminar(String nombre) {
            store.remove(nombre.toLowerCase());
        }
    }

    @Test
    void registerPlayerRejectsBlankOrDuplicate() {
        PlayerController controller = new PlayerController(new InMemoryPlayerRepository());

        assertNull(controller.registerPlayer("  "));
        assertNotNull(controller.registerPlayer("Ana"));
        assertNull(controller.registerPlayer("Ana"));
    }

    @Test
    void getPlayerReturnsExistingIgnoringCase() {
        PlayerController controller = new PlayerController(new InMemoryPlayerRepository());
        controller.registerPlayer("Ana");

        assertNotNull(controller.getPlayer("aNa"));
    }

    @Test
    void listPlayersReturnsEmptyArrayWhenNone() {
        PlayerController controller = new PlayerController(new InMemoryPlayerRepository());

        assertEquals(0, controller.listPlayers().length);
    }

    @Test
    void updateStatsRejectsNonFinishedGame() {
        PlayerController controller = new PlayerController(new InMemoryPlayerRepository());
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertEquals(ControllerResult.INVALID_INPUT, controller.updateStats(game));
    }

    @Test
    void updateStatsUpdatesWinnerAndLoser() {
        InMemoryPlayerRepository repo = new InMemoryPlayerRepository();
        PlayerController controller = new PlayerController(repo);

        Player black = new Player("Black");
        Player white = new Player("White");
        Game game = new Game(4, black, white);

        PieceColor[][] full = new PieceColor[4][4];
        for (int r = 0; r < 4; r++) {
            for (int c = 0; c < 4; c++) {
                full[r][c] = PieceColor.BLACK;
            }
        }
        game.getTablero().cargarEstado(full);

        assertEquals(ControllerResult.SUCCESS, controller.updateStats(game));
        assertEquals(1, black.getGanadas());
        assertEquals(1, white.getPerdidas());
    }

    @Test
    void deletePlayerReturnsNotFoundOrSuccess() {
        PlayerController controller = new PlayerController(new InMemoryPlayerRepository());

        assertEquals(ControllerResult.NOT_FOUND, controller.deletePlayer("Ana"));
        controller.registerPlayer("Ana");
        assertEquals(ControllerResult.SUCCESS, controller.deletePlayer("Ana"));
    }
}

