package com.nebula.electricity.foundation.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.explosion.Fire;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

/**
 * Stores all information about the simulation world
 * including objects, tiles, etc.
 */
public class World implements Module {
    // Associated renderer
    public WorldRenderer renderer;
    // Objects
    private HashMap<UUID, WorldObject> allObjects;
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

        // Initialise object map
        allObjects = new HashMap<>();

        // Selected coordinate
        selectedCoord = new Vector2i(-1, -1);

        // Centre camera position
        ElectricitySimulator.getCamera().translate(
                Constants.SCALED_TILE_SIZE.x * Constants.WORLD_SIZE.x * 0.5f,
                Constants.SCALED_TILE_SIZE.y * Constants.WORLD_SIZE.y * 0.5f
        );
    }

    @Override
    public void draw (SpriteBatch batch, ShapeRenderer shapes) {
        List<WorldObject> objects = allObjects.values().stream().sorted(Comparator.comparingInt(a -> -a.getPos().y)).collect(Collectors.toList());
        renderer.draw(batch, objects);
    }

    @Override
    public void dispose () {
        renderer.dispose();
    }

    // Object methods
    public Collection<WorldObject> getAllObjects () { return allObjects.values(); }

    public void forEachElectricalObject (Consumer<WorldObject> func) {
        allObjects.values().forEach(object -> {
            if (object.isElectric())
                func.accept(object);
        });
    }

    public <T extends WorldObject> boolean addObject (T object) {
        // Make sure position is valid
        if (!object.withinWorldBounds()) return false;
        if (canPlace(object)) return false;

        allObjects.put(UUID.randomUUID(), object);

        // Electrical
        object.initElectricProperties();
        if (object.isElectric())
            ELECTRICITY.VERTICES.add(object.getElectricProperties().getNodes());

        return true;
    }

    public void setFireAt (Vector2i pos) {
        if (!pos.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y))
            return;

        if (renderer.fires.stream().anyMatch(fire -> fire.position.equals(pos)))
            return;

        renderer.fires.add(new Fire(pos));
        renderer.fires.sort(Comparator.comparingInt(a -> -a.position.y));
    }

    public WorldObject getObject (UUID id) {
        return allObjects.get(id);
    }

    public boolean removeObject (UUID id) {
        WorldObject object = allObjects.get(id);

        // Remove influence from circuits
        if (object.isElectric()) {
            ELECTRICITY.VERTICES.delete(object.getElectricProperties().getNodes());

            for (Connection ref : ELECTRICITY.CONNECTIONS.getAllIDs()) {
                if (object.getElectricProperties().containsNode(ref.getID1())
                || object.getElectricProperties().containsNode(ref.getID2()))
                    ELECTRICITY.CONNECTIONS.queueDelete(ref);
            }

            ELECTRICITY.CONNECTIONS.flush();
        }

        return allObjects.remove(id) != null;
    }

    public void reset () {
        allObjects.clear();
        ELECTRICITY.CONNECTIONS.clear();
        ELECTRICITY.VERTICES.clear();
        ELECTRICITY.CIRCUITS.clear();
        renderer.fires.clear();
    }

    public Optional<UUID> objectAt (Vector2i pos) {
        if (!pos.withinBounds(Constants.LIMITS.x, Constants.LIMITS.y))
            return Optional.empty();

        for (Map.Entry<UUID, WorldObject> entry : allObjects.entrySet())
            if (entry.getValue().occupiedAt(pos)) return Optional.of(entry.getKey());
        return Optional.empty();
    }

    public Optional<UUID> idOf (WorldObject object) {
        for (Map.Entry<UUID, WorldObject> entry : allObjects.entrySet())
            if (entry.getValue().equals(object)) return Optional.of(entry.getKey());
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
