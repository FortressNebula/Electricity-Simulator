package com.nebula.electricity.content.world;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Config;
import com.nebula.electricity.math.Vector2i;

public class SimpleObjectType extends WorldObjectType {
    TextureAtlas.AtlasRegion texture;
    String textureName;

    public SimpleObjectType(String textureName) {
        super();
        this.textureName = textureName;
    }

    @Override
    protected void load () {
        super.load();
        texture = ElectricitySimulator.getTexture("objects/" + textureName);
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
        batch.draw(texture, object.position.x * Config.SCALED_TILE_SIZE.x, object.position.y * Config.SCALED_TILE_SIZE.y,
                32 * Config.SCALE, 43 * Config.SCALE);
    }
}
