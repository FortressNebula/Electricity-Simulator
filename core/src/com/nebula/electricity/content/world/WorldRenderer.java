package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.math.Vector2i;

public class WorldRenderer implements Disposable {
    final Texture backgroundTiles;
    final TextureRegion light;
    final TextureRegion dark;

    public WorldRenderer () {
        backgroundTiles = new Texture("textures/background_tiles.png");
        light = new TextureRegion(backgroundTiles, 32, 35);
        dark = new TextureRegion(backgroundTiles, 32, 0, 32, 35);
    }

    public void draw (SpriteBatch batch, Camera camera, int width, int height, Vector2i selected) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        for (int y = width - 1; y >= 0; y--) {
            for (int x = 0; x < height; x++) {
                if (x == selected.x && y == selected.y) batch.setColor(Color.GREEN);
                else batch.setColor(Color.WHITE);

                batch.draw((x + y) % 2 == 0 ? light : dark, x * Config.SCALED_TILE_SIZE.x, y * Config.SCALED_TILE_SIZE.y - 8 * Config.SCALE,
                        Config.SCALED_TILE_SPRITE_SIZE.x, Config.SCALED_TILE_SPRITE_SIZE.y);
            }
        }
        batch.end();
    }

    @Override
    public void dispose () {
        backgroundTiles.dispose();
    }
}
