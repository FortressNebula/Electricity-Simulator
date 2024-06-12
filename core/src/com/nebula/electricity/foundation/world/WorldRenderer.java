package com.nebula.electricity.foundation.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Disposable;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.world.object.WorldObject;

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

    public void drawGround (SpriteBatch batch) {
        // Draw tiles
        for (int y = Constants.WORLD_SIZE.y - 1; y >= 0; y--) {
            for (int x = 0; x < Constants.WORLD_SIZE.x; x++) {
                batch.draw((x + y) % 2 == 0 ? light : dark, x * Constants.SCALED_TILE_SIZE.x, y * Constants.SCALED_TILE_SIZE.y,
                        Constants.SCALED_TILE_SIZE.x, Constants.SCALED_TILE_SIZE.y);

                if (y != 0) continue;

                // Draw walls too
                batch.draw((x + y) % 2 == 0 ? lightWall : darkWall, x * Constants.SCALED_TILE_SIZE.x, -8 * Constants.SCALE,
                        Constants.SCALED_TILE_SIZE.x, 8 * Constants.SCALE);
            }
        }
    }

    public void draw (SpriteBatch batch, Array<WorldObject> objects) {
        ElectricitySimulator.setRenderModeAndStart(true, false);

        batch.setColor(Color.WHITE);

        for (WorldObject object : objects) object.draw(batch);
    }

    @Override
    public void dispose () {
    }
}
