package repository;

import model.Game;

public interface GameRepository {

    void guardar(String id, Game partida);

    Game buscarPorId(String id);

    Game[] obtenerTodas();

    void eliminar(String id);
}

