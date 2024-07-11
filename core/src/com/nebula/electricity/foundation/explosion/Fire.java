package com.nebula.electricity.foundation.explosion;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public class Fire {
    public Vector2i position;
    int frame;
    int ticks;
    final int frameDuration = 10;

    public Fire (Vector2i position) {
        this.position = position;
        this.frame = ElectricitySimulator.RANDOM.nextInt(0, 3);
        this.ticks = 0;
    }

    public void draw (TextureAtlas.AtlasRegion texture, SpriteBatch batch) {
        if (ticks++ >= frameDuration) {
            ticks = 0;
            frame = (frame + 1) % 3;
        }

        batch.draw(texture, position.x * Constants.SCALED_TILE_SIZE.x, position.y * Constants.SCALED_TILE_SIZE.y,
                Constants.SCALE * texture.getRegionWidth(), Constants.SCALE * texture.getRegionHeight());
    }

    public int getFrame () {
        return frame;
    }
}
