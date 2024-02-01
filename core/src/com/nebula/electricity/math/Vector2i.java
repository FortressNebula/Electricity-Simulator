package com.nebula.electricity.math;

import com.badlogic.gdx.math.MathUtils;

public class Vector2i {
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
}
