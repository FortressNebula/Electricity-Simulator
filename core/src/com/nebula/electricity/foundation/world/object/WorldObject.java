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
    protected String type;

    // Graphics methods
    public void draw (SpriteBatch batch) {
        batch.draw(getTexture(), position.x * Constants.SCALED_TILE_SIZE.x, position.y * Constants.SCALED_TILE_SIZE.y,
                getTexture().originalWidth * Constants.SCALE, getTexture().originalHeight * Constants.SCALE);
    }

    protected abstract TextureAtlas.AtlasRegion getTexture ();

    // Property methods
    public WorldObjectProperties getProperties () { return properties; }
    protected WorldObjectProperties initProperties () { return new WorldObjectProperties(); }

    // Collision methods
    public boolean occupiedAt (Vector2i pos) {
        if (pos.x < position.x) return false;
        if (pos.y < position.y) return false;
        if (pos.x > position.x + getSize().x - 1) return false;
        if (pos.y > position.y + getSize().y - 1) return false;

        return true;
    }

    public boolean intersectsWith (Vector2i pos, Vector2i size) {
        if (pos.x + size.x - 1 < position.x) return false;
        if (pos.y + size.y - 1 < position.y) return false;
        if (pos.x > position.x + getSize().x - 1) return false;
        if (pos.y > position.y + getSize().y - 1) return false;

        return true;
    }

    public boolean withinWorldBounds () {
        return position.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y) &&
                position.add(getSize()).add(-1).withinBounds(Constants.LIMITS.x, Constants.LIMITS.y);
    }

    public Vector2i getPos () {
        return position;
    }
    public void setPos (Vector2i newPos) {
        position = newPos;
    }
    public abstract Vector2i getSize ();
}
