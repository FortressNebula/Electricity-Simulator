package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.foundation.world.object.WorldObjectProperties;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.function.Consumer;

public class BulbObject extends SimpleObject {
    TextureAtlas.AtlasRegion glass;
    TextureAtlas.AtlasRegion glow;

    @Override
    protected WorldObjectProperties initProperties () {
        return super.initProperties()
                .add("on", false);
    }

    @Override
    public ElectricProperties createElectricProperties () {
        return ElectricProperties.make()
                .resistance(6f)
                .addNode(0, 0, Direction.LEFT)
                .addNode(0, 0, Direction.UP)
                .addNode(0, 0, Direction.RIGHT)
                .addNode(0, 0, Direction.DOWN)
                .buildAt(position, getSize(), Direction.LEFT);
    }

    @Override
    public void onClick (boolean left) {
        getProperties().set("on", !getProperties().getBool("on"));
    }

    @Override
    public void draw (SpriteBatch batch) {
        super.draw(batch);

        batch.setColor(1,1,1,0.4f);
        drawObjectTexture(batch, glass);

        if (getProperties().getBool("on")) {
            batch.setColor(1, 1, 1, 0.9f);
            drawObjectTexture(batch, glow, new Vector2i(-16, 0));
        }

        batch.setColor(1,1,1,1);
    }

    @Override
    public void drawGhost (SpriteBatch batch) {
        super.draw(batch);
    }

    // Texture utils
    public static Consumer<BulbObject> textures (String name) {
        return obj -> {
            obj.texture = ElectricitySimulator.getObjectTexture(name + "/bulb");
            obj.glass = ElectricitySimulator.getObjectTexture(name + "/glass");
            obj.glow = ElectricitySimulator.getObjectTexture(name + "/glow");
        };
    }
}
