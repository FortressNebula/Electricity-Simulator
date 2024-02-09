package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.world.object.WorldObject;

public class AxisObject extends WorldObject {
    public TextureAtlas.AtlasRegion horizontal;
    public TextureAtlas.AtlasRegion vertical;

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        return properties.getBool("horizontal") ? horizontal : vertical;
    }
}
