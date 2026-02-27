package repository;

import model.Player;

public interface PlayerRepository {

    void agregar(Player jugador);

    Player buscarPorNombre(String nombre);

    Player[] obtenerTodos();

    boolean existe(String nombre);

    void actualizar(Player jugador);

    void eliminar(String nombre);
}
