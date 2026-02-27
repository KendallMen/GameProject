package repository;

import model.Game;

public interface GameRepository {

    /**
     * Guarda una partida usando un identificador.
     * @param id identificador o ruta
     * @param partida partida a guardar
     */
    void guardar(String id, Game partida);

    /**
     * Busca una partida por identificador.
     * @param id identificador o ruta
     * @return partida encontrada o null
     */
    Game buscarPorId(String id);

    Game[] obtenerTodas();

    /**
     * Elimina una partida por identificador.
     * @param id identificador o ruta
     */
    void eliminar(String id);
}
