package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

public class DirectionalObject extends WorldObject {
    public TextureAtlas.AtlasRegion up;
    public TextureAtlas.AtlasRegion down;
    public TextureAtlas.AtlasRegion left;
    public TextureAtlas.AtlasRegion right;

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        switch (properties.getDirection()) {
            case UP: return up;
            case DOWN: return down;
            case LEFT: return left;
            case RIGHT: return right;
            default: throw new IllegalStateException("Directional objects must have 'direction' property!");
        }
    }

    @Override
    protected Vector2i getSize () {
        return properties.getDirection().rotate(size);
    }
}
