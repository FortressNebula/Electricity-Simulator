package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

public class Connection {
    Vector2i position;
    Direction direction;

    public Connection (Vector2i position, Direction direction) {
        this.position = position;
        this.direction = direction;
    }

    public Connection moveTo (Vector2i newPosition) {
        position = newPosition.add(position);
        return this;
    }

    public Connection rotate (Vector2i size, Direction newDirection) {
        position = newDirection.rotate(position, size);
        direction = direction.cycle(1 - newDirection.ordinal());
        return this;
    }

    public Vector2i getRenderPosition () {
        return position
                .mul(Constants.SCALED_TILE_SIZE)
                .add(getDirectionalOffset())
                .add(new Vector2i(-20, 20));
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
}
