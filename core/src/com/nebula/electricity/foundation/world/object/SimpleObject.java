package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.math.Vector2i;

public class SimpleObject extends WorldObject {
    TextureAtlas.AtlasRegion texture;

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        return texture;
    }

    @Override
    public Vector2i getSize () {
        return size;
    }
}
