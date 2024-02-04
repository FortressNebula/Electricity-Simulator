package com.nebula.electricity.content.content.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.content.world.WorldObject;
import com.nebula.electricity.content.world.WorldObjectType;
import com.nebula.electricity.math.Vector2i;

public class CubeObjectType extends WorldObjectType {
    TextureAtlas.AtlasRegion cube;

    @Override
    protected void make () {
        super.make();
        cube = ElectricitySimulator.getTexture("objects/cube");
    }

    @Override
    public void init (WorldObject object) {

    }

    @Override
    public Vector2i getSize (WorldObject object) {
        return Vector2i.ONE_BY_ONE;
    }

    @Override
    public void draw (SpriteBatch batch, WorldObject object) {
        batch.draw(cube, object.position.x * Config.SCALED_TILE_SIZE.x, object.position.y * Config.SCALED_TILE_SIZE.y,
                32 * Config.SCALE, 43 * Config.SCALE);
    }
}
