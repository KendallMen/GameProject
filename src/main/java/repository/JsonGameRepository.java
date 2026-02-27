package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Game;
import model.PieceColor;
import model.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;

/**
 * Persistencia en JSON para partidas.
 * Formato:
 * {
 *   "size": N,
 *   "turn": "BLACK"|"WHITE",
 *   "black": {"name":"..."},
 *   "white": {"name":"..."},
 *   "board": [["EMPTY","BLACK",...], [...]]
 * }
 */
public class JsonGameRepository implements GameRepository {

    private final Path baseDir;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public JsonGameRepository(Path baseDir) {
        this.baseDir = Objects.requireNonNull(baseDir, "baseDir");
        try { Files.createDirectories(baseDir); } catch (IOException ignored) {}
    }

    private Path resolve(String id) {
        String trimmed = id.trim();
        if (trimmed.contains("/") || trimmed.contains("\\") || Path.of(trimmed).isAbsolute()) {
            Path p = Path.of(trimmed);
            if (!p.getFileName().toString().endsWith(".json")) {
                p = p.getParent() == null
                        ? Path.of(p.getFileName() + ".json")
                        : p.getParent().resolve(p.getFileName() + ".json");
            }
            return p;
        }
        return baseDir.resolve(trimmed + ".json");
    }

    private static class GameDTO {
        int size;
        PieceColor turn;
        PlayerDTO black;
        PlayerDTO white;
        PieceColor[][] board;
    }
    private static class PlayerDTO { String name; }

    @Override
    public void guardar(String id, Game partida) {
        if (id == null || id.isBlank() || partida == null) throw new IllegalArgumentException();

        Path file = resolve(id);

        GameDTO dto = new GameDTO();
        dto.size = partida.getTablero().getTamano();
        dto.turn = partida.getTurno();

        dto.black = new PlayerDTO();
        dto.black.name = partida.getJugadorNegro().getNombre();

        dto.white = new PlayerDTO();
        dto.white.name = partida.getJugadorBlanco().getNombre();

        dto.board = partida.getTablero().copiarEstado();

        String json = gson.toJson(dto);
        try {
            Files.writeString(file, json, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Game buscarPorId(String id) {
        if (id == null || id.isBlank()) return null;

        Path file = resolve(id);
        if (!Files.exists(file)) return null;

        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            GameDTO dto = gson.fromJson(json, GameDTO.class);
            if (dto == null) return null;

            Player black = new Player(dto.black == null ? "Black" : dto.black.name);
            Player white = new Player(dto.white == null ? "White" : dto.white.name);

            Game game = new Game(dto.size, black, white);

            // Turno: el constructor inicia BLACK.
            if (dto.turn == PieceColor.WHITE) {
                game.cambiarTurno();
            }

            if (dto.board != null) {
                game.getTablero().cargarEstado(dto.board);
            }

            return game;

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override public Game[] obtenerTodas() { return new Game[0]; }

    @Override
    public void eliminar(String id) {
        try { Files.deleteIfExists(resolve(id)); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
}
