package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;
import java.util.UUID;

public class PlacingInputState extends InputState {
    int currentIndex;
    WorldObject currentObject;
    boolean isValid;

    float alpha;
    int frameCounter;

    boolean canPlaceYet;

    public PlacingInputState (Vector2i screenPos) {
        currentIndex = 0;

        resetObjectAt(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenPos), false);
        isValid = currentObject.withinWorldBounds();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);

        alpha = 0f;

        canPlaceYet = false;
    }

    @Override
    public void update () {
        alpha = (float) Math.abs(Math.sin((frameCounter++) / 30f));
    }

    @Override
    public void draw (SpriteBatch batch, ShapeRenderer shapes) {
        ElectricitySimulator.setRenderModeAndStart(true, false);

        batch.setColor(isValid ? 0.1f : 1, isValid ? 1 : 0.1f, 0.2f, alpha / 2);
        currentObject.drawGhost(batch);
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void mouseMoved (Vector2i screenPos) {
        Vector2i coords = currentObject.getPosFromCentrePos(ElectricitySimulator.unproject(screenPos));
        currentObject.setPos(coords.div(Constants.SCALED_TILE_SIZE));
        isValid = currentObject.withinWorldBounds();
    }

    @Override
    public boolean keyDown (int code) {
        if (code == Input.Keys.UP || code == Input.Keys.DOWN) {
            currentIndex += (code == Input.Keys.UP ? 1 : -1) + WorldObjectCreator.getNumCreators();
            currentIndex = currentIndex % WorldObjectCreator.getNumCreators();

            resetObject();
            return true;
        }

        if (code == Input.Keys.LEFT || code == Input.Keys.RIGHT) {
            currentObject.getProperties().rotateIfPossible(code == Input.Keys.LEFT);
            return true;
        }

        if (code == Input.Keys.R) {
            ElectricitySimulator.WORLD.reset();
        }

        return false;
    }

    @Override
    boolean touchUp (Vector2i screenPos, int pointer, int button) {
        canPlaceYet = true;
        return false;
    }

    @Override
    public boolean touchDown (Vector2i screenPos, int pointer, int button) {
        if (!canPlaceYet)
            return false;

        if (button == Input.Buttons.LEFT && isValid)
            return add(currentObject.getPos());

        if (button == Input.Buttons.RIGHT)
            return remove(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenPos));

        if (button == Input.Buttons.MIDDLE)
            return pick(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenPos));

        return false;
    }

    private boolean add (Vector2i coords) {
        if (!ElectricitySimulator.WORLD.addObject(currentObject))
            return false;

        resetObjectAt(coords, true);
        return true;
    }

    private boolean remove (Vector2i coords) {
        if (!ElectricitySimulator.WORLD.occupiedAt(coords))
            return false;

        Optional<UUID> id = ElectricitySimulator.WORLD.objectAt(coords);
        if (!id.isPresent())
            return false;

        return ElectricitySimulator.WORLD.removeObject(id.get());
    }

    private boolean pick (Vector2i coords) {
        currentIndex = ElectricitySimulator.WORLD.objectAt(coords)
                        .map(uuid -> WorldObjectCreator.getIndexFomObject(ElectricitySimulator.WORLD.getObject(uuid)))
                        .orElse(currentIndex);

        return true;
    }

    // Utility methods to make it easier to reset the object

    private void resetObjectAt (Vector2i at, boolean withSameProperties) {
        WorldObject newObject = WorldObjectCreator.getCreatorFromIndex(currentIndex).create(at);
        if (withSameProperties)
            newObject.getProperties().from(currentObject.getProperties());

        currentObject = newObject;
        isValid = currentObject.withinWorldBounds();
    }

    private void resetObject () {
        resetObjectAt(currentObject.getPos(), false);
    }
}
