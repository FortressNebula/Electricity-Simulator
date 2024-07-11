package com.nebula.electricity.foundation.world;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.utils.Disposable;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.explosion.Fire;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.ArrayList;
import java.util.List;

public class WorldRenderer implements Disposable {
    final TextureAtlas.AtlasRegion light;
    final TextureAtlas.AtlasRegion dark;
    final TextureAtlas.AtlasRegion lightWall;
    final TextureAtlas.AtlasRegion darkWall;

    final TextureAtlas.AtlasRegion[] fireSpriteSheet;
    final TextureAtlas.AtlasRegion fireGlow;

    public List<Fire> fires;

    public WorldRenderer () {
        light = ElectricitySimulator.getTexture("background/light");
        dark = ElectricitySimulator.getTexture("background/dark");

        lightWall = ElectricitySimulator.getTexture("background/light_wall");
        darkWall = ElectricitySimulator.getTexture("background/dark_wall");

        fireSpriteSheet = new TextureAtlas.AtlasRegion[] {
                ElectricitySimulator.getTexture("background/fire1"),
                ElectricitySimulator.getTexture("background/fire2"),
                ElectricitySimulator.getTexture("background/fire3"),

        };
        fireGlow = ElectricitySimulator.getGUITexture("hover_overlay");

        fires = new ArrayList<>();
    }

    public void drawBackground (SpriteBatch batch) {
        // Draw tiles
        for (int y = Constants.WORLD_SIZE.y - 1; y >= 0; y--) {
            for (int x = 0; x < Constants.WORLD_SIZE.x; x++) {
                int finalX = x;
                int finalY = y;

                batch.draw((x + y) % 2 == 0 ? light : dark, x * Constants.SCALED_TILE_SIZE.x, y * Constants.SCALED_TILE_SIZE.y,
                        Constants.SCALED_TILE_SIZE.x, Constants.SCALED_TILE_SIZE.y);

                if (fires.stream().anyMatch(fire -> fire.position.equals(new Vector2i(finalX, finalY)))) {
                    batch.setColor(1, ElectricitySimulator.RANDOM.nextFloat(0.7f, 0.8f), 0.5f, 0.5f);
                    batch.draw(fireGlow, x * Constants.SCALED_TILE_SIZE.x, y * Constants.SCALED_TILE_SIZE.y,
                            Constants.SCALED_TILE_SIZE.x, Constants.SCALED_TILE_SIZE.y);
                    batch.setColor(1, 1, 1, 1);
                }

                if (y != 0) continue;

                // Draw walls too
                batch.draw((x + y) % 2 == 0 ? lightWall : darkWall, x * Constants.SCALED_TILE_SIZE.x, -8 * Constants.SCALE,
                        Constants.SCALED_TILE_SIZE.x, 8 * Constants.SCALE);
            }
        }

        // Draw fire
        for (Fire fire : fires)
            fire.draw(fireSpriteSheet[fire.getFrame()], batch);
    }

    public void draw (SpriteBatch batch, List<WorldObject> objects) {
        ElectricitySimulator.setRenderModeAndStart(true, false);

        batch.setColor(Color.WHITE);

        for (WorldObject object : objects) object.draw(batch);
    }

    @Override
    public void dispose () {
    }
}
