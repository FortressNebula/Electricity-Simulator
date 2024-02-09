package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public class WorldObject {
    // Visual information
    Vector2i position; // Bottom-left corner
    Vector2i size;
    Vector2i max;
    // Behavioural information
    final WorldObjectProperties properties;
    boolean isTicking;
    // Rendering information
    TextureAtlas.AtlasRegion texture;

    public WorldObject () {
        size = Vector2i.INVALID;
        position = Vector2i.INVALID;
        max = Vector2i.INVALID;
        isTicking = false;
        properties = new WorldObjectProperties();
    }

    public void draw (SpriteBatch batch) {
        batch.draw(texture, position.x * Constants.SCALED_TILE_SIZE.x, position.y * Constants.SCALED_TILE_SIZE.y,
                texture.originalWidth * Constants.SCALE, texture.originalHeight * Constants.SCALE);
    }

    // Collision methods
    public boolean occupiedAt (Vector2i coords) {
        if (coords.x < position.x) return false;
        if (coords.y < position.y) return false;
        if (coords.x > position.x + size.x - 1) return false;
        if (coords.y > position.y + size.y - 1) return false;

        return true;
    }

    public boolean withinWorldBounds () {
        return position.withinBounds(Constants.WORLD_SIZE.x, Constants.WORLD_SIZE.y) &&
                max.withinBounds(Constants.WORLD_SIZE.x, Constants.WORLD_SIZE.y);
    }
    public Vector2i getPos () {
        return position;
    }
}
