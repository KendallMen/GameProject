package repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import model.Player;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class JsonPlayerRepository implements PlayerRepository {

    private final Path file;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private List<Player> cache;

    private static class PlayerDTO { String name; int wins; int losses; }
    private static class PlayersDTO { List<PlayerDTO> players = new ArrayList<>(); }

    public JsonPlayerRepository(Path file) {
        this.file = Objects.requireNonNull(file, "file");
        try { if (file.getParent() != null) Files.createDirectories(file.getParent()); } catch (IOException ignored) {}
        load();
    }

    private void load() {
        cache = new ArrayList<>();
        if (!Files.exists(file)) return;
        try {
            String json = Files.readString(file, StandardCharsets.UTF_8);
            PlayersDTO dto = gson.fromJson(json, PlayersDTO.class);
            if (dto != null && dto.players != null) {
                for (PlayerDTO p : dto.players) {
                    Player pl = new Player(p.name);
                    for (int i = 0; i < p.wins; i++) pl.sumarGanada();
                    for (int i = 0; i < p.losses; i++) pl.sumarPerdida();
                    cache.add(pl);
                }
            }
        } catch (IOException ignored) {}
    }

    private void save() {
        PlayersDTO dto = new PlayersDTO();
        for (Player p : cache) {
            PlayerDTO d = new PlayerDTO();
            d.name = p.getNombre();
            d.wins = p.getGanadas();
            d.losses = p.getPerdidas();
            dto.players.add(d);
        }
        String json = gson.toJson(dto);
        try { Files.writeString(file, json, StandardCharsets.UTF_8); } catch (IOException e) { throw new RuntimeException("Error guardando jugadores", e); }
    }

    @Override
    public void agregar(Player jugador) {
        if (jugador == null || jugador.getNombre() == null || jugador.getNombre().isBlank()) {
            throw new IllegalArgumentException("Nombre invalido");
        }
        if (existe(jugador.getNombre())) {
            throw new IllegalArgumentException("Jugador ya existe");
        }
        cache.add(jugador);
        save();
    }

    @Override
    public Player buscarPorNombre(String nombre) {
        if (nombre == null) return null;
        for (Player p : cache) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) return p;
        }
        return null;
    }

    @Override
    public Player[] obtenerTodos() {
        return cache.toArray(new Player[0]);
    }

    @Override
    public boolean existe(String nombre) {
        if (nombre == null) return false;
        for (Player p : cache) {
            if (p.getNombre().equalsIgnoreCase(nombre.trim())) return true;
        }
        return false;
    }

    @Override
    public void actualizar(Player jugador) {
        if (jugador == null || jugador.getNombre() == null) return;
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getNombre().equalsIgnoreCase(jugador.getNombre())) {
                cache.set(i, jugador);
                save();
                return;
            }
        }
        // si no estaba, agregar
        cache.add(jugador);
        save();
    }

    @Override
    public void eliminar(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("Nombre invalido");
        }
        for (int i = 0; i < cache.size(); i++) {
            if (cache.get(i).getNombre().equalsIgnoreCase(nombre.trim())) {
                cache.remove(i);
                save();
                return;
            }
        }
        throw new IllegalArgumentException("No existe un jugador con ese nombre");
    }
}
