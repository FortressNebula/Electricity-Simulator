package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.events.Events;
import com.nebula.electricity.math.Vector2i;

public abstract class WorldObject {
    // Visual information
    protected Vector2i position; // Bottom-left corner
    protected Vector2i size;
    // Behavioural information
    protected WorldObjectProperties properties;
    protected ElectricProperties electricProperties;
    protected String type;
    private boolean isElectric = true;

    // Behavioural methods
    public void onClick (boolean left) {}

    public void onCircuitUpdate () {}

    // Override for electric objects
    // Called when object is added to the list of world objects
    protected ElectricProperties createElectricProperties () {
        isElectric = false; // When overridden, don't call super! so this line isn't called.
        return null;
    }

    public ElectricProperties getElectricProperties () {
        return electricProperties;
    }

    public boolean isElectric () { return isElectric; }

    public void initElectricProperties () {
        electricProperties = createElectricProperties();

        if (!isElectric)
            return;

        Events.CIRCUIT_UPDATE.add(this::onCircuitUpdate);
        electricProperties.onConnectionDestroyed = this::onCircuitUpdate;
    }

    // Graphics methods
    public void draw (SpriteBatch batch) {
        drawObjectTexture(batch, getTexture());
    }

    /**
     * Special function called when drawing the ghost of an object for the object placement manager. Should not modify the batch's colour.
     * @param batch the sprite batch
     */
    public void drawGhost (SpriteBatch batch) {
        draw(batch);
    }

    protected void drawObjectTexture(SpriteBatch batch, TextureAtlas.AtlasRegion texture) {
        batch.draw(texture, position.x * Constants.SCALED_TILE_SIZE.x, position.y * Constants.SCALED_TILE_SIZE.y,
                texture.originalWidth * Constants.SCALE, texture.originalHeight * Constants.SCALE);
    }

    protected void drawObjectTexture(SpriteBatch batch, TextureAtlas.AtlasRegion texture, Vector2i offset) {
        batch.draw(texture,
                position.x * Constants.SCALED_TILE_SIZE.x + Constants.SCALE * offset.x,
                position.y * Constants.SCALED_TILE_SIZE.y + Constants.SCALE * offset.y,
                texture.originalWidth * Constants.SCALE,
                texture.originalHeight * Constants.SCALE);
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

    public Vector2i getPosFromCentrePos (Vector2i unprojectedCoords) {
        if (getSize() == Vector2i.ONE_BY_ONE) return unprojectedCoords;
        return unprojectedCoords
                .add(getSize()
                        .mul(Constants.SCALED_TILE_SIZE)
                        .div(-2) // Centre point
                ).add(Constants.SCALED_TILE_SIZE.div(2)); // Fix
    }

    public abstract Vector2i getSize ();
}
