package app;

import controller.GameController;
import controller.PlayerController;
import repository.GameRepository;
import repository.PlayerRepository;
import repository.JsonGameRepository;
import repository.JsonPlayerRepository;
import view.swing.MenuFrame;

import java.nio.file.Path;

/**
 * Arranque minimo para verificar compilacion.
 */
public class Main {

    public static void main(String[] args) {
        // Directorio base para guardar partidas
        Path dataDir = Path.of("data");
        GameRepository gameRepository = new JsonGameRepository(dataDir.resolve("games"));
        PlayerRepository playerRepository = new JsonPlayerRepository(dataDir.resolve("players.json"));
        GameController controller = new GameController(gameRepository);
        PlayerController playerController = new PlayerController(playerRepository);

        MenuFrame.show(controller, playerController);
    }
}
