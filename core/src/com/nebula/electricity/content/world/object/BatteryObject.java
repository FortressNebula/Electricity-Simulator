package com.nebula.electricity.content.world.object;

import com.nebula.electricity.foundation.electricity.ElectricObjectHandler;
import com.nebula.electricity.math.Direction;

public class BatteryObject extends DirectionalObject {
    @Override
    public ElectricObjectHandler makeElectricHandler () {
        return ElectricObjectHandler.make()
                .addConnection(0, 0, Direction.LEFT)
                .addConnection(1, 0, Direction.RIGHT)
                .buildAt(position, getSize(), properties.getDirection());
    }
}
