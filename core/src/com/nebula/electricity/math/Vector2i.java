package com.nebula.electricity.math;

import com.badlogic.gdx.math.MathUtils;

import java.util.Objects;

public class Vector2i {
    public static final Vector2i ONE_BY_ONE = new Vector2i(1, 1);
    public static final Vector2i ZERO = new Vector2i(0, 0);
    public static final Vector2i INVALID = new Vector2i(-1, -1);

    public int x, y;

    public Vector2i (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i (float x, float y, boolean shouldFloor) {
        if (!shouldFloor) throw new IllegalStateException("Oops, didn't prepare for that.");

        this.x = MathUtils.floor(x);
        this.y = MathUtils.floor(y);
    }

    public static Vector2i mul (Vector2i vec, int s) {
        return new Vector2i(vec.x * s, vec.y * s);
    }

    @Override
    public String toString () {
        return String.format("(%d, %d)", x, y);
    }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vector2i vector2i = (Vector2i) o;
        return x == vector2i.x && y == vector2i.y;
    }

    @Override
    public int hashCode () {
        return Objects.hash(x, y);
    }
}
