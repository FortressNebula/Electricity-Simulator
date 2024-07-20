package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.Electricity;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

public class Node implements CircuitVertex {
    Vector2i position;
    Direction direction;
    Vector2i renderPosition;
    Vector2i directionOffset;

    boolean isEditable;
    boolean isConnected;
    boolean isEnabled;
    boolean isTransformDirty;

    int id;

    public Node (int x, int y, Direction direction) {
        this(new Vector2i(x, y), direction);
    }

    public Node (Vector2i position, Direction direction) {
        this.position = position;
        this.direction = direction;
        renderPosition = Vector2i.INVALID;
        directionOffset = Vector2i.INVALID;
        isEditable = true;
        isConnected = false;
        isEnabled = true;
        isTransformDirty = true;
        id = Electricity.GLOBAL_VERTEX_ID++;
    }

    public Node moveTo (Vector2i newPosition) {
        position = newPosition.add(position);
        isTransformDirty = true;
        return this;
    }

    public Node rotate (Vector2i size, Direction newDirection) {
        position = newDirection.rotate(position, size);
        direction = direction.cycle(newDirection.ordinal() - 1);
        isTransformDirty = true;
        return this;
    }

    @Override
    public Vector2i getRenderPosition () {
        if (isTransformDirty) updateTransform();

        return renderPosition;
    }

    @Override
    public Vector2i getCombinedPosition () {
        if (isTransformDirty) updateTransform();

        return renderPosition.add(directionOffset);
    }

    private void updateTransform () {
        renderPosition = position
                .mul(Constants.SCALED_TILE_SIZE)
                .add(getTileAlignmentOffset())
                .add(-20, 20);

        if (direction != Direction.DOWN)
            directionOffset = Vector2i.fromDirection(direction).mul(4 * Constants.SCALE);
        else
            directionOffset = Vector2i.ZERO;

        isTransformDirty = false;
    }

    @Override
    public boolean canConnect () {
        return isEnabled && !isConnected && isEditable;
    }

    private Vector2i getTileAlignmentOffset () {
        switch (direction) {
            case LEFT: return new Vector2i(0, Constants.SCALED_TILE_SIZE.y / 2);
            case RIGHT: return new Vector2i(Constants.SCALED_TILE_SIZE.x, Constants.SCALED_TILE_SIZE.y / 2);
            case UP: return new Vector2i(Constants.SCALED_TILE_SIZE.x / 2, Constants.SCALED_TILE_SIZE.y);
            case DOWN: return new Vector2i(Constants.SCALED_TILE_SIZE.x / 2, 0);
        }
        return Vector2i.ZERO;
    }

    public Direction getDirection () { return direction; }
    public Vector2i getPosition () { return position; }

    public boolean getEnabled () { return isEnabled; }
    public boolean getConnected () { return isConnected; }
    public void setEnabled(boolean value) { isEnabled = value; }
    public void setConnected(boolean value) { isConnected = value; }

    public void lock () {
        isEditable = false;
    }
    public void unlock () {
        isEditable = true;
    }
    public boolean isLocked () {
        return !isEditable;
    }

    @Override
    public int getID () { return id; }
}
