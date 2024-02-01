package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.OrderedMap;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.content.Module;
import com.nebula.electricity.math.Vector2i;

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
        allObjects = new OrderedMap<>();

        // Selected coordinate
        selectedCoord = new Vector2i(-1, -1);

        forEach((x, y) -> map[x][y] = new Tile(Tile.Type.EMPTY));

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
        renderer.draw(batch, camera, width, height, selectedCoord);
    }

    @Override
    public void dispose () {
        renderer.dispose();
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
