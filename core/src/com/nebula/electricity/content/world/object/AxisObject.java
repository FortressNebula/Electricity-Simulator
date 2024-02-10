package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

public class AxisObject extends WorldObject {
    public TextureAtlas.AtlasRegion horizontal;
    public TextureAtlas.AtlasRegion vertical;

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        return properties.getBool("horizontal") ? horizontal : vertical;
    }

    @Override
    protected Vector2i getSize () {
        return properties.getBool("horizontal") ? size : size.swap();
    }
}
