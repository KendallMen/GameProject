package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DiscTest {

    @Test
    void constructorStoresColor() {
        Disc disc = new Disc('B');
        assertEquals('B', disc.getColor());
    }

    @Test
    void colorOponenteFromBlackIsWhite() {
        assertEquals('W', Disc.colorOponente('B'));
    }

    @Test
    void colorOponenteFromWhiteIsBlack() {
        assertEquals('B', Disc.colorOponente('W'));
    }

    @Test
    void colorOponenteFromLowercaseReturnsBlack() {
        assertEquals('B', Disc.colorOponente('w'));
    }

    @Test
    void colorOponenteFromUnknownReturnsBlack() {
        assertEquals('B', Disc.colorOponente('X'));
    }
}

