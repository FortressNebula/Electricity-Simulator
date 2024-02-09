package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public abstract class WorldObject {
    // Visual information
    protected Vector2i position; // Bottom-left corner
    protected Vector2i size;
    Vector2i max;
    // Behavioural information
    protected WorldObjectProperties properties;
    boolean isTicking;

    // Graphics methods
    public void draw (SpriteBatch batch) {
        batch.draw(getTexture(), position.x * Constants.SCALED_TILE_SIZE.x, position.y * Constants.SCALED_TILE_SIZE.y,
                getTexture().originalWidth * Constants.SCALE, getTexture().originalHeight * Constants.SCALE);
    }

    protected abstract TextureAtlas.AtlasRegion getTexture ();

    // Property methods
    public WorldObjectProperties getProperties () { return properties; }

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
