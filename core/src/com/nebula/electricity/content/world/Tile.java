package com.nebula.electricity.content.world;

public class Tile {
    private Type type;

    public Tile (Type type) {
        this.type = type;
    }

    public enum Type {
        EMPTY,
        OBJECT,
        WIRE
    }
}
