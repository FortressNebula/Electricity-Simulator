package com.nebula.electricity.math;

public enum Direction {
    UP   (true ),
    LEFT (false),
    DOWN (true ),
    RIGHT(false)
    ;

    final boolean swapXandY;

    Direction (boolean swapXandY) {
        this.swapXandY = swapXandY;
    }

    // Rotate a vector pointing LEFT to a vector pointing in this direction
    public Vector2i rotate (Vector2i v) {
        return swapXandY ? v.swap() : v;
    }

    // Rotate a vector pointing in a given direction to a vector pointing in this direction
    public Vector2i rotate (Vector2i v, Direction from) {
        return (swapXandY ^ from.swapXandY) ? v.swap() : v;
    }

    public Direction cycle (int amount) {
        int index = ordinal() + amount;
        return values()[index < 0 ? index + values().length : index];
    }

    public Direction next () {
        return cycle(1);
    }

    public Direction prev () {
        return cycle(-1);
    }
}
