package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nebula.electricity.content.content.events.AllEvents;
import com.nebula.electricity.content.events.Observer;
import com.nebula.electricity.math.Vector2i;

/**
 * Represents a set of characteristics that a world object can exhibit.
 * Should be registered in AllWorldObjectTypes, and be able to initialise a given world object
 */
public abstract class WorldObjectType implements Observer {
    public boolean isTicking;

    public WorldObjectType () {
        AllEvents.INIT.add(this);
    }

    /**
     * On init, initialise the object type
     */
    @Override
    public void notify (String name) {
        load();
    }

    /**
     * Its unsafe to create textures the second the class is loaded, so defining the world object types
     * is deferred until its safe to do so
     */
    protected void load () {
        isTicking = false;
    }

    /**
     * Create a new World Object and fill out its properties table
     * @param object the object to be created according to the object type specification
     */
    public abstract void init (WorldObject object);

    public abstract Vector2i getSize (WorldObject object);

    public void tick (WorldObject object) {}

    public abstract void draw (SpriteBatch batch, WorldObject object);
}
