package com.nebula.electricity.foundation.world;

public class Tile {
    private boolean hasWire;

    public Tile () {
        hasWire = false;
    }

    public boolean isOccupied () {
        return !hasWire;
    }
}
