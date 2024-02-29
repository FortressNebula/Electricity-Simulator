package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;
import java.util.UUID;

public class PlacingInputState implements InputState {
    int currentIndex;
    WorldObject currentObject;
    boolean isValid;

    float alpha;
    int frameCounter;

    public PlacingInputState (int screenX, int screenY, boolean pick) {
        currentIndex = getIndex(screenX, screenY, pick);

        resetObjectAt(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY), false);
        isValid = currentObject.withinWorldBounds();
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);

        alpha = 0f;
    }

    private int getIndex (int screenX, int screenY, boolean pick) {
        if (!pick)
            return 0;

        Optional<UUID> id =
                ElectricitySimulator.WORLD.objectAt(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY));
        return id
                .map(uuid -> WorldObjectCreator.getIndexFomObject(ElectricitySimulator.WORLD.getObject(uuid)))
                .orElse(0);

    }

    @Override
    public void update () {
        alpha = (float) Math.abs(Math.sin((frameCounter++) / 30f));
    }

    @Override
    public void draw (SpriteBatch batch, Camera camera) {
        batch.setProjectionMatrix(camera.combined);
        batch.setColor(isValid ? 0.1f : 1, isValid ? 1 : 0.1f, 0.2f, alpha / 2);
        currentObject.draw(batch);
        batch.setColor(1, 1, 1, 1);
    }

    @Override
    public void mouseMoved (int screenX, int screenY) {
        Vector2i coords = currentObject.getPosFromCentrePos(ElectricitySimulator.unproject(screenX, screenY));
        currentObject.setPos(coords.div(Constants.SCALED_TILE_SIZE));
        isValid = currentObject.withinWorldBounds();
    }

    @Override
    public InputActionResult keyDown (int code) {
        if (code == Input.Keys.UP || code == Input.Keys.DOWN) {
            currentIndex += (code == Input.Keys.UP ? 1 : -1) + WorldObjectCreator.getNumCreators();
            currentIndex = currentIndex % WorldObjectCreator.getNumCreators();

            resetObject();
            return InputActionResult.success(this);
        }

        if (code == Input.Keys.LEFT || code == Input.Keys.RIGHT) {
            currentObject.getProperties().rotateIfPossible(code == Input.Keys.LEFT);
            return InputActionResult.success(this);
        }

        return InputActionResult.failure(this);
    }

    @Override
    public InputActionResult touchDown (int screenX, int screenY, int pointer, int button) {
        if (button == Input.Buttons.LEFT && isValid)
            return add(currentObject.getPos());

        if (button == Input.Buttons.RIGHT)
            return remove(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY));

        return fail();
    }

    private InputActionResult add (Vector2i coords) {
        if (!ElectricitySimulator.WORLD.addObject(currentObject))
            return softFail();

        resetObjectAt(coords, true);
        return success();
    }

    private InputActionResult remove (Vector2i coords) {
        if (!ElectricitySimulator.WORLD.occupiedAt(coords))
            return softFail();

        Optional<UUID> id = ElectricitySimulator.WORLD.objectAt(coords);
        if (!id.isPresent())
            return softFail();

        if (!ElectricitySimulator.WORLD.removeObject(id.get()))
            return softFail();

        return success();
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

    // Utility methods to make it easier to return an action result

    private InputActionResult fail () {
        return InputActionResult.failure(new InputManager.DefaultInputState());
    }

    private InputActionResult softFail () {
        return InputActionResult.failure(this);
    }

    private InputActionResult success () {
        return InputActionResult.success(this);
    }
}
