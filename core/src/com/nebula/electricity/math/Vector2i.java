package com.nebula.electricity.math;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;

import java.util.Objects;

public class Vector2i {
    public static final Vector2i ONE_BY_ONE = new Vector2i(1, 1);
    public static final Vector2i ZERO = new Vector2i(0, 0);
    public static final Vector2i INVALID = new Vector2i(-1, -1);

    public int x, y;

    public Vector2i (Vector3 v) {
        this(v.x, v.y, true);
    }

    public Vector2i (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Vector2i (float x, float y, boolean shouldFloor) {
        if (!shouldFloor) throw new IllegalStateException("Oops, didn't prepare for that.");

        this.x = MathUtils.floor(x);
        this.y = MathUtils.floor(y);
    }

    public boolean withinBounds (int minX, int minY, int maxX, int maxY) {
        if (x < minX || x > maxX) return false;
        if (y < minY || y > maxY) return false;
        return true;
    }

    public boolean withinBounds (int maxX, int maxY) {
        return withinBounds(0, 0, maxX, maxY);
    }

    public boolean withinBounds (Vector2i min, Vector2i max) {
        return withinBounds(min.x, min.y, max.x, max.y);
    }

    public boolean isValid () { return x >= 0 && y >= 0; }

    // Math methods
    public Vector2i cpy () { return new Vector2i(x, y); }
    public Vector2i swap () { return new Vector2i(y, x); }

    public Vector2i mul (int s) { return new Vector2i(x * s, y * s); }
    public Vector2i mul (Vector2i v) { return new Vector2i(x * v.x, y * v.y); }

    public Vector2i div (float s) { return new Vector2i(x / s, y / s, true); }
    public Vector2i div (Vector2i v) { return new Vector2i((float) x / v.x, (float) y / v.y, true); }

    public Vector2i add (Vector2i v) { return new Vector2i(x + v.x, y + v.y); }
    public Vector2i add (int s) { return new Vector2i(x + s, y + s); }

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
