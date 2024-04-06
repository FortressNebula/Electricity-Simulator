package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectProperties;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.function.Consumer;

public class DirectionalObject extends WorldObject {
    public TextureAtlas.AtlasRegion up;
    public TextureAtlas.AtlasRegion down;
    public TextureAtlas.AtlasRegion left;
    public TextureAtlas.AtlasRegion right;

    @Override
    protected WorldObjectProperties initProperties () {
        return super.initProperties()
                .add("direction", Direction.RIGHT);
    }

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
    public Vector2i getSize () {
        return properties.getDirection().fakeRotate(size);
    }

    // Texture utils
    public static Consumer<DirectionalObject> textures (String name) {
        return obj -> {
            obj.up = ElectricitySimulator.getObjectTexture(name + "/up");
            obj.down = ElectricitySimulator.getObjectTexture(name + "/down");
            obj.left = ElectricitySimulator.getObjectTexture(name + "/left");
            obj.right = ElectricitySimulator.getObjectTexture(name + "/right");
        };
    }
}
