package model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PieceColorTest {

    @Test
    void oppositeFromBlackIsWhite() {
        assertEquals(PieceColor.WHITE, PieceColor.BLACK.opposite());
    }

    @Test
    void oppositeFromWhiteIsBlack() {
        assertEquals(PieceColor.BLACK, PieceColor.WHITE.opposite());
    }

    @Test
    void oppositeFromEmptyIsEmpty() {
        assertEquals(PieceColor.EMPTY, PieceColor.EMPTY.opposite());
    }

    @Test
    void oppositeTwiceReturnsSameForBlack() {
        assertEquals(PieceColor.BLACK, PieceColor.BLACK.opposite().opposite());
    }

    @Test
    void oppositeTwiceReturnsSameForWhite() {
        assertEquals(PieceColor.WHITE, PieceColor.WHITE.opposite().opposite());
    }
}

