package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.math.Vector2i;

public class WorldRenderer implements Disposable {
    final TextureAtlas.AtlasRegion light;
    final TextureAtlas.AtlasRegion dark;
    final TextureAtlas.AtlasRegion lightWall;
    final TextureAtlas.AtlasRegion darkWall;

    public WorldRenderer () {
        light = ElectricitySimulator.getTexture("background/light");
        dark = ElectricitySimulator.getTexture("background/dark");

        lightWall = ElectricitySimulator.getTexture("background/light_wall");
        darkWall = ElectricitySimulator.getTexture("background/dark_wall");
    }

    public void draw (SpriteBatch batch, Camera camera, int width, int height, Vector2i selected) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        // Draw tiles
        for (int y = width - 1; y >= 0; y--) {
            for (int x = 0; x < height; x++) {
                if (x == selected.x && y == selected.y) batch.setColor(Color.GREEN);
                else batch.setColor(Color.WHITE);

                batch.draw((x + y) % 2 == 0 ? light : dark, x * Config.SCALED_TILE_SIZE.x, y * Config.SCALED_TILE_SIZE.y,
                        Config.SCALED_TILE_SIZE.x, Config.SCALED_TILE_SIZE.y);

                if (y != 0) continue;

                // Draw walls too
                batch.draw((x + y) % 2 == 0 ? lightWall : darkWall, x * Config.SCALED_TILE_SIZE.x, -8 * Config.SCALE,
                        Config.SCALED_TILE_SIZE.x, 8 * Config.SCALE);
            }
        }
        batch.end();
    }

    @Override
    public void dispose () {
    }
}
