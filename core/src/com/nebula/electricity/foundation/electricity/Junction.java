package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public class Junction implements CircuitVertex {
    Vector2i position;
    int id;

    public Junction (Vector2i position) {
        this.position = position;
        id = CircuitManager.GLOBAL_VERTEX_ID++;
    }

    @Override
    public int getID () {
        return id;
    }

    @Override
    public Vector2i getRenderPosition () {
        return position
                .mul(Constants.SCALED_TILE_SIZE)
                .add(12 * Constants.SCALE, 9 * Constants.SCALE);
    }

    public Vector2i getPosition () { return position; }

    @Override
    public boolean canConnect () {
        return true;
    }
}
