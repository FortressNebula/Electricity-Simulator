package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.utils.ObjectMap;
import com.nebula.electricity.math.Direction;

@SuppressWarnings("unused")
public class WorldObjectProperties {
    private final ObjectMap<String, Object> propertyMap = new ObjectMap<>();

    // Setters
    public WorldObjectProperties add (String name, Object value) {
        propertyMap.put(name, value);
        return this;
    }
    // Will only set the property if it already exists
    public WorldObjectProperties set (String name, Object value) {
        if (propertyMap.containsKey(name)) propertyMap.put(name, value);
        return this;
    }
    public void reset () { propertyMap.clear(); }

    // Getters
    public <T> T get (String name, Class<T> clazz) {
        return (T) propertyMap.get(name);
    }
    public boolean getBool (String name) {
        return get(name, Boolean.class);
    }
    public float getFloat (String name) {
        return get(name, Float.class);
    }
    public Direction getDirection () {
        return getDirection("direction");
    }
    public Direction getDirection (String name) {
        return get(name, Direction.class);
    }
}
