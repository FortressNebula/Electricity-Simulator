package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectMap;
import com.badlogic.gdx.utils.OrderedMap;
import com.badlogic.gdx.utils.Pool;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.content.Module;
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
    private Pool<WorldObject> objectPool;
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
        this.width = Config.WORLD_SIZE.x;
        this.height = Config.WORLD_SIZE.y;
        map = new Tile[width][height];

        // Initialise object management thingys
        allObjects = new OrderedMap<>();
        objectPool = new Pool<WorldObject>() {
            @Override
            protected WorldObject newObject () {
                return new WorldObject();
            }
        };

        // Selected coordinate
        selectedCoord = new Vector2i(-1, -1);

        forEach((x, y) -> map[x][y] = new Tile());

        // Centre camera position
        ElectricitySimulator.getCamera().translate(
                Config.SCALED_TILE_SIZE.x * width * 0.5f,
                Config.SCALED_TILE_SIZE.y * height * 0.5f
        );
    }

    @Override
    public boolean doesDraw () {
        return true;
    }

    @Override
    public void update () {

    }

    @Override
    public void draw (SpriteBatch batch, Camera camera) {
        Array<WorldObject> objects = allObjects.values().toArray();
        objects.sort(Comparator.comparingInt(a -> -a.position.y));
        renderer.draw(batch, camera, width, height, objects);
    }

    @Override
    public void dispose () {
        renderer.dispose();
    }

    // Object methods

    public void newObject (WorldObjectType type, Vector2i position) {
        WorldObject object = objectPool.obtain();
        object.initialise(position, type);
        allObjects.put(UUID.randomUUID(), object);
    }

    public WorldObject getObject (UUID id) {
        return allObjects.get(id);
    }

    public boolean removeObject (UUID id) {
        return allObjects.remove(id) != null;
    }

    public Optional<UUID> objectAt (Vector2i pos) {
        for (ObjectMap.Entry<UUID, WorldObject> entry : allObjects.iterator())
            if (entry.value.occupiedAt(pos)) return Optional.of(entry.key);
        return Optional.empty();
    }

    public boolean occupiedAt (Vector2i pos) {
        for (WorldObject obj : allObjects.values())
            if (obj.occupiedAt(pos)) return true;
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
     * @param x the x position on the screen
     * @param y the y position on the screen
     * @return the integer coordinates of the tile, with (0, 0) being in the bottom-left
     */
    public Vector2i coordinatesFromScreenPos (float x, float y) {
        Vector3 coords = ElectricitySimulator.getCamera(false).unproject(new Vector3(x, y, 0));
        return new Vector2i(coords.x / Config.SCALED_TILE_SIZE.x, coords.y / Config.SCALED_TILE_SIZE.y, true);
    }

    public Vector2i coordinatesFromScreenPos (Vector2 vec) {
        return coordinatesFromScreenPos(vec.x, vec.y);
    }
}
