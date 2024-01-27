package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nebula.electricity.content.Module;
import com.nebula.electricity.content.tile.Tile;

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

    // Module implementation methods
    public void init (int width, int height) {
        // Create renderer
        renderer = new WorldRenderer();

        // Populate map with tiles
        this.width = width;
        this.height = height;
        map = new Tile[width][height];

        forEach((x, y) -> map[x][y] = new Tile(Tile.Type.EMPTY));
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
        renderer.draw(batch, camera);
    }

    @Override
    public void dispose () {
        renderer.dispose();
    }

    // Utility methods
    public void forEach (BiConsumer<Integer, Integer> cons) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cons.accept(x, y);
            }
        }
    }
}
