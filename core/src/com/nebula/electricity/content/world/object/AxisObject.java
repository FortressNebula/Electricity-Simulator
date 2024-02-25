package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectProperties;
import com.nebula.electricity.math.Vector2i;

import java.util.function.Consumer;

public class AxisObject extends WorldObject {
    public TextureAtlas.AtlasRegion horizontal;
    public TextureAtlas.AtlasRegion vertical;

    @Override
    protected WorldObjectProperties initProperties () {
        return super.initProperties()
                .add("horizontal", true);
    }

    @Override
    protected TextureAtlas.AtlasRegion getTexture () {
        return properties.getBool("horizontal") ? horizontal : vertical;
    }

    @Override
    public Vector2i getSize () {
        return properties.getBool("horizontal") ? size : size.swap();
    }

    // Texture utils
    public static Consumer<AxisObject> textures (String name) {
        return obj -> {
            obj.horizontal = ElectricitySimulator.getObjectTexture(name + "/horizontal");
            obj.vertical = ElectricitySimulator.getObjectTexture(name + "/vertical");
        };
    }
}
