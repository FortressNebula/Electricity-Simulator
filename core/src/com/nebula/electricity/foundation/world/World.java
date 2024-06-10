package com.nebula.electricity.foundation.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.ConnectionReference;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.*;
import java.util.function.Consumer;

/**
 * Stores all information about the simulation world
 * including objects, tiles, etc.
 */
public class World implements Module {
    // Associated renderer
    private WorldRenderer renderer;
    // Map information
    private int width, height;
    // Objects
    private OrderedMap<UUID, WorldObject> allObjects;
    // DEBUG
    public Vector2i selectedCoord;

    // Module implementation methods

    /**
     * Initialising the world with a height and width defined by the config.
     * Creates the renderer, tile map, object list, as well as correcting the camera position.
     */
    @Override
    public void init () {
        // Create renderer
        renderer = new WorldRenderer();

        // Populate map with tiles
        this.width = Constants.WORLD_SIZE.x;
        this.height = Constants.WORLD_SIZE.y;

        // Initialise object map
        allObjects = new OrderedMap<>();

        // Selected coordinate
        selectedCoord = new Vector2i(-1, -1);

        // Centre camera position
        ElectricitySimulator.getCamera().translate(
                Constants.SCALED_TILE_SIZE.x * width * 0.5f,
                Constants.SCALED_TILE_SIZE.y * height * 0.5f
        );
    }

    @Override
    public void draw (SpriteBatch batch) {
        Array<WorldObject> objects = allObjects.values().toArray();
        objects.sort(Comparator.comparingInt(a -> -a.getPos().y));
        renderer.draw(batch, width, height, objects);
    }

    @Override
    public void dispose () {
        renderer.dispose();
    }

    // Object methods
    public Array<WorldObject> getAllObjects () { return allObjects.values().toArray(); }

    public void forEachObject (Consumer<WorldObject> func) {
        allObjects.forEach(entry -> func.accept(entry.value));
    }

    public void forEachElectricalObject (Consumer<WorldObject> func) {
        allObjects.forEach(entry -> {
            if (entry.value.isElectric())
                func.accept(entry.value);
        });
    }

    public <T extends WorldObject> boolean addObject (T object) {
        // Make sure position is valid
        if (!object.withinWorldBounds()) return false;
        if (canPlace(object)) return false;

        // Electrical
        object.initElectricProperties();
        if (object.isElectric())
            ElectricitySimulator.CIRCUIT_MANAGER.registerVerticies(object.getElectricProperties().getNodes());

        allObjects.put(UUID.randomUUID(), object);
        return true;
    }

    public WorldObject getObject (UUID id) {
        return allObjects.get(id);
    }

    public boolean removeObject (UUID id) {
        WorldObject object = allObjects.get(id);

        // Remove influence from circuits
        if (object.isElectric()) {
            ElectricitySimulator.CIRCUIT_MANAGER.deleteVertices(object.getElectricProperties().getNodes());

            for (ConnectionReference ref : ElectricitySimulator.CIRCUIT_MANAGER.getAllConnections()) {
                if (object.getElectricProperties().containsNode(ref.getID1())
                || object.getElectricProperties().containsNode(ref.getID2()))
                    ElectricitySimulator.CIRCUIT_MANAGER.queueDeleteConnection(ref);
            }

            ElectricitySimulator.CIRCUIT_MANAGER.flushDeletionQueue();
        }

        return allObjects.remove(id) != null;
    }

    public void clearObjects () {
        allObjects.clear();
        ElectricitySimulator.CIRCUIT_MANAGER.clearConnections();
    }

    public Optional<UUID> objectAt (Vector2i pos) {
        if (!pos.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y))
            return Optional.empty();

        for (ObjectMap.Entry<UUID, WorldObject> entry : allObjects.iterator())
            if (entry.value.occupiedAt(pos)) return Optional.of(entry.key);
        return Optional.empty();
    }

    public boolean occupiedAt (Vector2i pos) {
        if (!pos.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y))
            return true;

        for (WorldObject obj : allObjects.values())
            if (obj.occupiedAt(pos)) return true;
        return false;
    }

    public boolean canPlace (WorldObject object) {
        return canPlace(object.getPos(), object.getSize());
    }
    public boolean canPlace (Vector2i pos, Vector2i size) {
        for (WorldObject obj : allObjects.values())
            if (obj.intersectsWith(pos, size)) return true;
        return false;
    }

    // Utility methods

    /**
     * Outputs tile coordinates from a given screen position
     * @param pos the mouse position on the screen
     * @return the integer coordinates of the tile, with (0, 0) being in the bottom-left
     */

    public Vector2i coordinatesFromScreenPos (Vector2i pos) {
        return ElectricitySimulator.unproject(pos).div(Constants.SCALED_TILE_SIZE);
    }
}
