package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.utils.ObjectMap;

public class WorldObjectProperties {
    private final ObjectMap<String, Object> propertyMap = new ObjectMap<>();

    // Setters
    public WorldObjectProperties set (String name, Object value) {
        propertyMap.put(name, value);
        return this;
    }
    public void reset () { propertyMap.clear(); }

    // Getters
    public Object get (String name) {
        return propertyMap.get(name);
    }
    public boolean getBool (String name) {
        return (boolean) get(name);
    }
    public float getFloat (String name) {
        return (float) get(name);
    }
}
