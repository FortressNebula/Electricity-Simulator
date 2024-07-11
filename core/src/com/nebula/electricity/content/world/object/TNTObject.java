package com.nebula.electricity.content.world.object;

import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.math.Direction;

public class TNTObject extends SimpleObject {
    @Override
    protected ElectricProperties createElectricProperties () {
        return new ElectricProperties()
                .addNode(0, 0, Direction.LEFT)
                .addNode(0, 0, Direction.UP)
                .addNode(0, 0, Direction.RIGHT)
                .addNode(0, 0, Direction.DOWN)
                .maxCurrent(1f)
                .buildAt(position, getSize(), Direction.LEFT);
    }
}
