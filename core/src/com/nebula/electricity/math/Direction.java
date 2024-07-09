package com.nebula.electricity.math;

public enum Direction {
    UP   (true ),
    RIGHT(false),
    DOWN (true ),
    LEFT (false)
    ;

    final boolean swapXandY;

    Direction (boolean swapXandY) {
        this.swapXandY = swapXandY;
    }

    // Rotate a vector pointing RIGHT to a vector pointing in this direction
    public Vector2i fakeRotate (Vector2i v) {
        return swapXandY ? v.swap() : v;
    }

    // Correctly rotate a vector pointing RIGHT to a vector pointing in this direction
    public Vector2i rotate (Vector2i v, Vector2i size) {
        switch (this) {
            case RIGHT:  return v;
            case LEFT: return new Vector2i(size.x - 1 - v.x, size.y - 1 - v.y);
            case UP:    return new Vector2i(size.x - 1 - v.y, v.x);
            case DOWN:  return new Vector2i(v.y, size.y - 1 - v.x);
        }

        return Vector2i.INVALID;
    }

    // Rotate a vector pointing in a given direction to a vector pointing in this direction
    public Vector2i fakeRotate (Vector2i v, Direction from) {
        return (swapXandY ^ from.swapXandY) ? v.swap() : v;
    }

    public Direction cycle (int amount) {
        int index = ordinal() + amount;
        return values()[index < 0 ? index + values().length : index % values().length];
    }

    public Direction next () {
        return cycle(1);
    }

    public Direction prev () {
        return cycle(-1);
    }
}
