package com.nebula.electricity.content.world.object;

import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.math.Direction;

public class BatteryObject extends DirectionalObject {
    @Override
    public ElectricProperties createElectricProperties () {
        return new ElectricProperties()
                .voltage(120f)
                .addNode(0, 0, Direction.LEFT)
                .addNode(1, 0, Direction.RIGHT)
                .buildAt(position, getSize(), properties.getDirection());
    }
}
