package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.utils.ObjectMap;

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
