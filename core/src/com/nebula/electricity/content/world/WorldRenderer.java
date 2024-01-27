package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Disposable;

public class WorldRenderer implements Disposable {
    final Texture backgroundTiles;
    final TextureRegion light;
    final TextureRegion dark;

    public WorldRenderer () {

        backgroundTiles = new Texture("textures/background_tiles.png");
        light = new TextureRegion(backgroundTiles, 32, 35);
        dark = new TextureRegion(backgroundTiles, 32, 0, 32, 35);
    }

    public void draw (SpriteBatch batch, Camera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        int s = 5;
        for (int y = 5; y > 0; y--) {
            for (int x = 0; x < 5; x++) {
                batch.draw((x + y) % 2 == 0 ? light : dark, 32*x*s, 27*y*s, 32*s, 35*s);
            }
        }
        batch.end();
    }

    @Override
    public void dispose () {
        backgroundTiles.dispose();
    }
}
