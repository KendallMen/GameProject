package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PlayerTest {

    @Test
    void constructorSetsName() {
        Player player = new Player("Ana");
        assertEquals("Ana", player.getNombre());
    }

    @Test
    void initialStatsAreZero() {
        Player player = new Player("Ana");
        assertEquals(0, player.getGanadas());
        assertEquals(0, player.getPerdidas());
    }

    @Test
    void sumarGanadaIncrementsWins() {
        Player player = new Player("Ana");
        player.sumarGanada();
        assertEquals(1, player.getGanadas());
    }

    @Test
    void sumarPerdidaIncrementsLosses() {
        Player player = new Player("Ana");
        player.sumarPerdida();
        assertEquals(1, player.getPerdidas());
    }

    @Test
    void winsAndLossesAccumulateIndependently() {
        Player player = new Player("Ana");
        player.sumarGanada();
        player.sumarGanada();
        player.sumarPerdida();

        assertEquals(2, player.getGanadas());
        assertEquals(1, player.getPerdidas());
    }
}

