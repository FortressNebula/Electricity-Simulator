package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.utils.ObjectMap;
import com.nebula.electricity.math.Direction;

@SuppressWarnings("unused")
public class WorldObjectProperties {
    private ObjectMap<String, Object> propertyMap = new ObjectMap<>();

    public boolean has (String name) {
        return propertyMap.containsKey(name);
    }

    public void from (WorldObjectProperties props) {
        propertyMap = new ObjectMap<>(props.propertyMap);
    }

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
    public WorldObjectProperties cycleDirection (int amount) {
        return set("direction", getDirection().cycle(amount));
    }
    public WorldObjectProperties cycleAxis () {
        return set("horizontal", !getBool("horizontal"));
    }
    public WorldObjectProperties rotateIfPossible (boolean forward) {
        if (propertyMap.containsKey("direction"))
            return cycleDirection(forward ? 1 : -1);
        if (propertyMap.containsKey("horizontal"))
            return cycleAxis();
        return this;
    }
    public void reset () { propertyMap.clear(); }

    // Getters
    public <T> T get (String name, Class<T> clazz) {
        return clazz.cast(propertyMap.get(name));
    }
    public boolean getBool (String name) {
        return get(name, Boolean.class);
    }
    public float getFloat (String name) {
        return get(name, Float.class);
    }
    public double getDouble (String name) {
        return get(name, Double.class);
    }
    public Direction getDirection () {
        return getDirection("direction");
    }
    public Direction getDirection (String name) {
        return get(name, Direction.class);
    }
}
