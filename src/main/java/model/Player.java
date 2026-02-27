package model;

/**
 * Representa un jugador del dominio.
 */
public class Player {
    private final String nombre;
    private int ganadas;
    private int perdidas;

    public Player(String nombre) {
        this.nombre = nombre;
        this.ganadas = 0;
        this.perdidas = 0;
    }

    public String getNombre() {
        return nombre;
    }

    public int getGanadas() {
        return ganadas;
    }

    public int getPerdidas() {
        return perdidas;
    }

    public void sumarGanada() {
        ganadas++;
    }

    public void sumarPerdida() {
        perdidas++;
    }
}

