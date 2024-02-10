package com.nebula.electricity.math;

public enum Direction {
    UP   (true ),
    DOWN (true ),
    LEFT (false),
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
}
