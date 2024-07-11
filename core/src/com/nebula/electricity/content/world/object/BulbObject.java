package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.foundation.world.object.WorldObjectProperties;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;
import java.util.function.Consumer;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class BulbObject extends SimpleObject {
    TextureAtlas.AtlasRegion glass;
    TextureAtlas.AtlasRegion glow;

    @Override
    protected WorldObjectProperties initProperties () {
        return super.initProperties()
                .add("brightness", 0D);
    }

    @Override
    public ElectricProperties createElectricProperties () {
        return new ElectricProperties()
                .resistance(30f)
                .addNode(0, 0, Direction.LEFT)
                .addNode(0, 0, Direction.UP)
                .addNode(0, 0, Direction.RIGHT)
                .addNode(0, 0, Direction.DOWN)
                .buildAt(position, getSize(), Direction.LEFT);
    }

    @Override
    public void onCircuitUpdate () {
        Optional<Connection> internalConnection = electricProperties.getInternalID();

        if (internalConnection.isPresent() && ELECTRICITY.CONNECTIONS.get(internalConnection.get()) != null) {
            getProperties().set("brightness", Math.abs(ELECTRICITY.CONNECTIONS.get(internalConnection.get())
                    .getCurrent()) / 5D);
            System.out.println(Math.abs(ELECTRICITY.CONNECTIONS.get(internalConnection.get())
                    .getCurrent()));
        } else {
            getProperties().set("brightness", 0D);
        }
    }

    @Override
    public void draw (SpriteBatch batch) {
        super.draw(batch);

        batch.setColor(1,1,1,0.4f);
        drawObjectTexture(batch, glass);

        batch.setColor(1, 1, 1, (float) MathUtils.clamp(getProperties().getDouble("brightness"), 0D, 1D));
        drawObjectTexture(batch, glow, new Vector2i(-16, 0));

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
