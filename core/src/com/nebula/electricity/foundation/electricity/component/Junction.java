package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.Electricity;
import com.nebula.electricity.math.Vector2i;

public class Junction implements CircuitVertex {
    Vector2i position;
    boolean isConnected;
    int id;

    public Junction (Vector2i position) {
        this.position = position;
        id = Electricity.GLOBAL_VERTEX_ID++;
    }

    @Override
    public int getID () {
        return id;
    }

    @Override
    public Vector2i getRenderPosition () {
        return position
                .mul(Constants.SCALED_TILE_SIZE)
                .add(Constants.SCALED_TILE_SIZE.x / 2, Constants.SCALED_TILE_SIZE.y / 2)
                .add(-20, 20);
    }

    @Override
    public Vector2i getCombinedPosition () {
        return getRenderPosition();
    }

    public Vector2i getPosition () { return position; }

    @Override
    public boolean canConnect () {
        return true;
    }

    public boolean getConnected () { return isConnected; }
    public void setConnected (boolean value) { isConnected = value; }

}
