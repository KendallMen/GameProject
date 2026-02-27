package controller;

import model.Game;
import model.Player;
import repository.PlayerRepository;

import java.util.Objects;

/**
 * Controlador de jugadores y estadisticas. Agnostico a la vista.
 */
public class PlayerController {

    private final PlayerRepository playerRepository;

    public PlayerController(PlayerRepository playerRepository) {
        this.playerRepository = Objects.requireNonNull(playerRepository, "playerRepository");
    }

    // ✅ Antes: registerPlayer(name, PlayerRef out)
    // Ahora: retorna el Player creado o null si no se pudo
    public Player registerPlayer(String name) {
        if (name == null) return null;
        name = name.trim();
        if (name.isBlank()) return null;
        if (playerRepository.existe(name)) return null;

        Player created = new Player(name);
        playerRepository.agregar(created);
        return created;
    }

    // ✅ Antes: getPlayer(name, PlayerRef out)
    // Ahora: retorna Player o null
    public Player getPlayer(String name) {
        if (name == null) return null;
        name = name.trim();
        if (name.isBlank()) return null;

        return playerRepository.buscarPorNombre(name);
    }

    // ✅ Antes: listPlayers(PlayersState out)
    // Ahora: retorna arreglo directo
    public Player[] listPlayers() {
        Player[] all = playerRepository.obtenerTodos();
        return (all == null) ? new Player[0] : all;
    }

    public ControllerResult updateStats(Game game) {
        if (game == null || !game.juegoTerminado()) {
            return ControllerResult.INVALID_INPUT;
        }

        int negras = game.contarNegras();
        int blancas = game.contarBlancas();
        if (negras == blancas) {
            return ControllerResult.SUCCESS;
        }

        Player ganador = negras > blancas ? game.getJugadorNegro() : game.getJugadorBlanco();
        Player perdedor = negras > blancas ? game.getJugadorBlanco() : game.getJugadorNegro();

        ganador.sumarGanada();
        perdedor.sumarPerdida();

        playerRepository.actualizar(ganador);
        playerRepository.actualizar(perdedor);
        return ControllerResult.SUCCESS;
    }

    public ControllerResult deletePlayer(String name) {
        if (name == null || name.isBlank()) {
            return ControllerResult.INVALID_INPUT;
        }
        if (!playerRepository.existe(name.trim())) {
            return ControllerResult.NOT_FOUND;
        }

        playerRepository.eliminar(name.trim());
        return ControllerResult.SUCCESS;
    }
}
