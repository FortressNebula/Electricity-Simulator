package com.nebula.electricity.content.world.object;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.electricity.component.ConnectionData;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.math.Direction;

import java.util.function.Consumer;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class SwitchObject extends SimpleObject {
    TextureAtlas.AtlasRegion on;
    TextureAtlas.AtlasRegion off;

    @Override
    protected ElectricProperties createElectricProperties () {
        return new SwitchElectricProperties()
                .addNode(0, 0, Direction.LEFT)
                .addNode(0, 0, Direction.UP)
                .addNode(0, 0, Direction.RIGHT)
                .addNode(0, 0, Direction.DOWN)
                .buildAt(position, getSize(), Direction.LEFT);
    }

    @Override
    public void onClick (boolean left) {
        if (!left)
            return;

        SwitchElectricProperties props = (SwitchElectricProperties) electricProperties;
        if (props.isOn)
            props.deactivate();
        else
            props.activate();
    }

    @Override
    public void draw (SpriteBatch batch) {
        if (electricProperties != null && ((SwitchElectricProperties) electricProperties).isOn)
            drawObjectTexture(batch, on);
        else
            drawObjectTexture(batch, off);
    }

    @Override
    public void drawGhost (SpriteBatch batch) {
        draw(batch);
    }

    // Texture utils
    public static Consumer<SwitchObject> textures (String name) {
        return obj -> {
            obj.on = ElectricitySimulator.getObjectTexture(name + "/on");
            obj.off = ElectricitySimulator.getObjectTexture(name + "/off");
        };
    }

    static class SwitchElectricProperties extends ElectricProperties {
        boolean isOn;

        protected SwitchElectricProperties () {
            super();
            shouldHandleInternalConnectionAutomatically = false;
        }

        void activate () {
            if (isOn)
                return;
            isOn = true;
            if (registeredConnectionID == null)
                return;

            ELECTRICITY.CONNECTIONS.add(Connection.of(registeredConnectionID),
                    ConnectionData.internal(voltage, resistance, registeredConnectionID.getID1() > registeredConnectionID.getID2()));
        }

        void deactivate () {
            if (!isOn)
                return;
            isOn = false;
            if (registeredConnectionID == null)
                return;

            ELECTRICITY.CONNECTIONS.delete(Connection.of(registeredConnectionID));
        }
    }
}
