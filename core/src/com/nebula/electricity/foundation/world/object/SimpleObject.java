package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;

public class SimpleObject extends WorldObject {
    TextureAtlas.AtlasRegion texture;

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        return texture;
    }
}
