package com.nebula.electricity.foundation.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.Comparator;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

/**
 * Stores all information about the simulation world
 * including objects, tiles, etc.
 */
public class World implements Module {
    // Associated renderer
    private WorldRenderer renderer;
    // Map information
    private int width, height;
    private Tile[][] map;
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
        map = new Tile[width][height];

        // Initialise object map
        allObjects = new OrderedMap<>();

        // Selected coordinate
        selectedCoord = new Vector2i(-1, -1);

        forEach((x, y) -> map[x][y] = new Tile());

        // Centre camera position
        ElectricitySimulator.getCamera().translate(
                Constants.SCALED_TILE_SIZE.x * width * 0.5f,
                Constants.SCALED_TILE_SIZE.y * height * 0.5f
        );
    }

    @Override
    public void draw (SpriteBatch batch, Camera camera) {
        Array<WorldObject> objects = allObjects.values().toArray();
        objects.sort(Comparator.comparingInt(a -> -a.getPos().y));
        renderer.draw(batch, camera, width, height, objects);
    }

    @Override
    public void dispose () {
        renderer.dispose();
    }

    // Object methods

    public <T extends WorldObject> boolean addObject (T object) {
        // Make sure position is valid
        if (!object.withinWorldBounds()) return false;
        if (canPlace(object)) return false;
        allObjects.put(UUID.randomUUID(), object);
        return true;
    }

    public WorldObject getObject (UUID id) {
        return allObjects.get(id);
    }

    public boolean removeObject (UUID id) {
        return allObjects.remove(id) != null;
    }

    public void clearObjects () { allObjects.clear(); }

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
        return map[pos.x][pos.y].isOccupied();
    }

    public boolean canPlace (WorldObject object) {
        return canPlace(object.getPos(), object.getSize());
    }
    public boolean canPlace (Vector2i pos, Vector2i size) {
        for (WorldObject obj : allObjects.values())
            if (obj.intersectsWith(pos, size)) return true;
        return map[pos.x][pos.y].isOccupied();
    }

    // Utility methods

    /**
     * Iterates over every tile in the map
     * @param cons A consumer, taking in (x, y) coordinates as inputs
     */
    public void forEach (BiConsumer<Integer, Integer> cons) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cons.accept(x, y);
            }
        }
    }

    /**
     * Outputs tile coordinates from a given screen position
     * @param pos the mouse position on the screen
     * @return the integer coordinates of the tile, with (0, 0) being in the bottom-left
     */

    public Vector2i coordinatesFromScreenPos (Vector2i pos) {
        return ElectricitySimulator.unproject(pos).div(Constants.SCALED_TILE_SIZE);
    }
}
