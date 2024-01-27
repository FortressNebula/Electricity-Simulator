package com.nebula.electricity.math;

public class Vector2i {
    public int x, y;

    public Vector2i (int x, int y) {
        this.x = x;
        this.y = y;
    }

    public static Vector2i mul(Vector2i vec, int s) {
        return new Vector2i(vec.x * s, vec.y * s);
    }
}
