package com.nebula.electricity.foundation;

import com.nebula.electricity.math.Vector2i;

public class Constants {
    public static final Vector2i WORLD_SIZE = new Vector2i(6, 6);
    public static final Vector2i LIMITS = WORLD_SIZE.add(-1);

    public static final int SCALE = 5;
    public static final Vector2i TILE_SIZE = new Vector2i(32, 27);
    public static final Vector2i SCALED_TILE_SIZE = TILE_SIZE.mul(SCALE);
}
