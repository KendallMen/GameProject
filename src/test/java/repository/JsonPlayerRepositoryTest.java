package repository;

import model.Player;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class JsonPlayerRepositoryTest {

    @TempDir
    Path tempDir;

    @Test
    void agregarAndBuscarPorNombrePersist() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        Player player = new Player("Ana");
        repo.agregar(player);

        assertNotNull(repo.buscarPorNombre("Ana"));
        assertNotNull(repo.buscarPorNombre("aNa"));

        JsonPlayerRepository repoReloaded = new JsonPlayerRepository(file);
        assertNotNull(repoReloaded.buscarPorNombre("Ana"));
    }

    @Test
    void agregarRejectsInvalidOrDuplicate() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        assertThrows(IllegalArgumentException.class, () -> repo.agregar(new Player("  ")));

        repo.agregar(new Player("Ana"));
        assertThrows(IllegalArgumentException.class, () -> repo.agregar(new Player("Ana")));
    }

    @Test
    void existeAndObtenerTodosReflectsCache() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        assertFalse(repo.existe("Ana"));
        repo.agregar(new Player("Ana"));

        assertTrue(repo.existe("Ana"));
        assertEquals(1, repo.obtenerTodos().length);
    }

    @Test
    void actualizarUpdatesExistingAndCanAddNew() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        Player ana = new Player("Ana");
        repo.agregar(ana);
        ana.sumarGanada();
        repo.actualizar(ana);

        Player loaded = repo.buscarPorNombre("Ana");
        assertNotNull(loaded);
        assertEquals(1, loaded.getGanadas());

        Player bob = new Player("Bob");
        repo.actualizar(bob);
        assertTrue(repo.existe("Bob"));
    }

    @Test
    void eliminarRemovesExistingAndRejectsMissing() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        repo.agregar(new Player("Ana"));
        repo.eliminar("Ana");
        assertFalse(repo.existe("Ana"));

        assertThrows(IllegalArgumentException.class, () -> repo.eliminar("NoExiste"));
    }

    @Test
    void statsArePersistedAcrossReload() {
        Path file = tempDir.resolve("players.json");
        JsonPlayerRepository repo = new JsonPlayerRepository(file);

        Player ana = new Player("Ana");
        repo.agregar(ana);
        ana.sumarGanada();
        ana.sumarPerdida();
        repo.actualizar(ana);

        JsonPlayerRepository repoReloaded = new JsonPlayerRepository(file);
        Player loaded = repoReloaded.buscarPorNombre("Ana");
        assertNotNull(loaded);
        assertEquals(1, loaded.getGanadas());
        assertEquals(1, loaded.getPerdidas());
    }
}