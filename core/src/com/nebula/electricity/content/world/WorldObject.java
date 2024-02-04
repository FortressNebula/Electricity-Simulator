package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.Pool;
import com.nebula.electricity.math.Vector2i;

public class WorldObject implements Pool.Poolable {
    public WorldObjectType type;
    public Vector2i position; // Bottom-left corner
    private ObjectMap<String, Object> properties;

    public WorldObject () {
        type = null;
        position = Vector2i.INVALID;
        properties = new ObjectMap<>();
    }

    public void initialise (Vector2i position, WorldObjectType type) {
        this.position = position;
        this.type = type;
        this.type.init(this);
    }

    public void draw (SpriteBatch batch) {
        type.draw(batch, this);
    }

    @Override
    public void reset () {
        position = Vector2i.INVALID;
        properties.clear();
        type = null;
    }

    // Property methods

    public void addProperty (String name, Object value) {
        properties.put(name, value);
    }

    public Object get (String name) {
        return properties.get(name);
    }

    public boolean getBool (String name) {
        return (boolean) get(name);
    }

    public float getFloat (String name) {
        return (float) get(name);
    }

    // Collision methods

    public boolean occupiedAt (Vector2i coords) {
        if (coords.x < position.x) return false;
        if (coords.y < position.y) return false;
        if (coords.x > position.x + type.getSize(this).x - 1) return false;
        if (coords.y > position.y + type.getSize(this).y - 1) return false;

        return true;
    }
}
