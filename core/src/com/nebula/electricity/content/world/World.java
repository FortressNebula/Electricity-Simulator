package com.nebula.electricity.content.world;

import com.nebula.electricity.content.Module;
import com.nebula.electricity.content.tile.Tile;

import java.util.function.BiConsumer;

/**
 * Stores all information about the simulation world
 * including objects, tiles, etc.
 */
public class World implements Module {
    private WorldRenderer renderer;

    private int width, height;
    private Tile[][] map;


    public void init (int width, int height) {
        // Create renderer
        renderer = new WorldRenderer();

        // Populate map with tiles
        this.width = width;
        this.height = height;
        map = new Tile[width][height];

        forEach((x, y) -> map[x][y] = new Tile(Tile.Type.EMPTY));
    }

    public void forEach (BiConsumer<Integer, Integer> cons) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                cons.accept(x, y);
            }
        }
    }

    @Override
    public boolean hasRenderer () {
        return true;
    }

    @Override
    public ModuleRenderer getRenderer () {
        return renderer;
    }

    @Override
    public void update () {

    }
}
