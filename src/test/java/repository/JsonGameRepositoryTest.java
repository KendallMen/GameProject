package repository;

import model.Game;
import model.PieceColor;
import model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonGameRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void guardarRejectsInvalidInput() {
        JsonGameRepository repo = new JsonGameRepository(tempDir);
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertThrows(IllegalArgumentException.class, () -> repo.guardar(null, game));
        assertThrows(IllegalArgumentException.class, () -> repo.guardar(" ", game));
        assertThrows(IllegalArgumentException.class, () -> repo.guardar("id", null));
    }

    @Test
    void guardarCreatesFileAndLoadRestoresState() throws Exception {
        JsonGameRepository repo = new JsonGameRepository(tempDir);
        Game game = new Game(4, new Player("A"), new Player("B"));

        assertTrue(game.jugar(0, 1));
        assertEquals(PieceColor.WHITE, game.getTurno());

        repo.guardar("g1", game);
        Path savedFile = tempDir.resolve("g1.json");
        assertTrue(Files.exists(savedFile));

        Game loaded = repo.buscarPorId("g1");
        assertNotNull(loaded);
        assertEquals(4, loaded.getTablero().getTamano());
        assertEquals(PieceColor.WHITE, loaded.getTurno());
        assertEquals(game.contarNegras(), loaded.contarNegras());
        assertEquals(game.contarBlancas(), loaded.contarBlancas());

        assertBoardsEqual(game.getTablero().copiarEstado(), loaded.getTablero().copiarEstado());
    }

    @Test
    void buscarPorIdReturnsNullWhenMissingOrInvalid() {
        JsonGameRepository repo = new JsonGameRepository(tempDir);

        assertNull(repo.buscarPorId("missing"));
        assertNull(repo.buscarPorId(" "));
    }

    @Test
    void eliminarRemovesFile() throws Exception {
        JsonGameRepository repo = new JsonGameRepository(tempDir);
        Game game = new Game(4, new Player("A"), new Player("B"));

        repo.guardar("g2", game);
        Path savedFile = tempDir.resolve("g2.json");
        assertTrue(Files.exists(savedFile));

        repo.eliminar("g2");
        assertFalse(Files.exists(savedFile));
    }

    @Test
    void guardarWithAbsolutePathWorks() throws Exception {
        JsonGameRepository repo = new JsonGameRepository(tempDir);
        Game game = new Game(4, new Player("A"), new Player("B"));

        Path target = tempDir.resolve("nested").resolve("game-save.json");
        Files.createDirectories(target.getParent());
        repo.guardar(target.toString(), game);

        assertTrue(Files.exists(target));
        assertNotNull(repo.buscarPorId(target.toString()));
    }

    private static void assertBoardsEqual(PieceColor[][] expected, PieceColor[][] actual) {
        assertEquals(expected.length, actual.length, "Board rows mismatch");
        for (int r = 0; r < expected.length; r++) {
            assertArrayEquals(expected[r], actual[r], "Row mismatch at " + r);
        }
    }
}
