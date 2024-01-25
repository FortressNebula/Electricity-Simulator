package com.nebula.electricity.content.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.nebula.electricity.content.Module;

public class WorldRenderer implements Module.ModuleRenderer {
    final SpriteBatch batch;
    final OrthographicCamera camera;

    final Texture backgroundTiles;
    final TextureRegion light;
    final TextureRegion dark;

    public WorldRenderer () {
        batch = new SpriteBatch();
        camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        backgroundTiles = new Texture("textures/background_tiles.png");
        light = new TextureRegion(backgroundTiles, 32, 35);
        dark = new TextureRegion(backgroundTiles, 32, 0, 32, 35);
    }

    @Override
    public void onResize (int width, int height) {
        camera.setToOrtho(false, width, height);
    }

    @Override
    public void draw () {
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
        batch.dispose();
        backgroundTiles.dispose();
    }
}
