package com.nebula.electricity.content;

import com.nebula.electricity.math.Vector2i;

public class Config {
    public static final Vector2i WORLD_SIZE = new Vector2i(6, 6);

    public static final Vector2i TILE_SIZE = new Vector2i(32, 27);
    public static final Vector2i TILE_SPRITE_SIZE = new Vector2i(32, 35);

    public static final int SCALE = 5;
    public static final Vector2i SCALED_TILE_SIZE = Vector2i.mul(TILE_SIZE, SCALE);
    public static final Vector2i SCALED_TILE_SPRITE_SIZE = Vector2i.mul(TILE_SPRITE_SIZE, SCALE);
}
