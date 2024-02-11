package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public abstract class WorldObject {
    // Visual information
    protected Vector2i position; // Bottom-left corner
    protected Vector2i size;
    // Behavioural information
    protected WorldObjectProperties properties;
    protected boolean isTicking;

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
        if (coords.x > position.x + getSize().x - 1) return false;
        if (coords.y > position.y + getSize().y - 1) return false;

        return true;
    }

    public boolean withinWorldBounds (Vector2i at) {
        return at.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y) &&
                at.add(getSize()).add(-1).withinBounds(Constants.LIMITS.x, Constants.LIMITS.y);
    }

    public boolean withinWorldBounds () {
        return withinWorldBounds(position);
    }

    public Vector2i getPos () {
        return position;
    }
    public void setPos (Vector2i newPos) {
        position = newPos;
    }
    protected abstract Vector2i getSize ();
}
