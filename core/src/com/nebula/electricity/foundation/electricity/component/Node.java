package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.CircuitManager;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

public class Node implements CircuitVertex {
    Vector2i position;
    Direction direction;
    boolean isConnected;
    boolean isEnabled;
    int id;

    public Node (int x, int y, Direction direction) {
        this(new Vector2i(x, y), direction);
    }

    public Node (Vector2i position, Direction direction) {
        this.position = position;
        this.direction = direction;
        isConnected = false;
        isEnabled = true;
        id = CircuitManager.GLOBAL_VERTEX_ID++;
    }

    public Node moveTo (Vector2i newPosition) {
        position = newPosition.add(position);
        return this;
    }

    public Node rotate (Vector2i size, Direction newDirection) {
        position = newDirection.rotate(position, size);
        direction = direction.cycle(1 - newDirection.ordinal());
        return this;
    }

    @Override
    public Vector2i getRenderPosition () {
        return position
                .mul(Constants.SCALED_TILE_SIZE)
                .add(getDirectionalOffset())
                .add(-20, 20);
    }

    @Override
    public boolean canConnect () {
        return isEnabled && !isConnected;
    }

    public Vector2i getDirectionalOffset () {
        switch (direction) {
            case LEFT: return new Vector2i(0, Constants.SCALED_TILE_SIZE.y / 2);
            case RIGHT: return new Vector2i(Constants.SCALED_TILE_SIZE.x, Constants.SCALED_TILE_SIZE.y / 2);
            case UP: return new Vector2i(Constants.SCALED_TILE_SIZE.x / 2, Constants.SCALED_TILE_SIZE.y);
            case DOWN: return new Vector2i(Constants.SCALED_TILE_SIZE.x / 2, 0);
        }
        return Vector2i.ZERO;
    }

    public boolean getEnabled () { return isEnabled; }
    public boolean getConnected () { return isConnected; }
    public void setEnabled(boolean value) { isEnabled = value; }
    public void setConnected(boolean value) { isConnected = value; }

    @Override
    public int getID () { return id; }
}
