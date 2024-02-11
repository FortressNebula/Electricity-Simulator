package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.world.AllWorldObjects;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;
import com.nebula.electricity.math.Vector2i;

public class PlacingInputState implements InputState {
    static final WorldObjectCreator<?>[] ALL_CREATORS = new WorldObjectCreator[] { AllWorldObjects.CUBE, AllWorldObjects.CYLINDER };
    int currentIndex;
    WorldObject currentObject;
    boolean isValid;

    float alpha;
    int frameCounter;

    public PlacingInputState (int screenX, int screenY) {
        currentIndex = 0;
        resetObjectAt(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY), false);
        isValid = true;
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Hand);

        alpha = 0f;
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
        Vector2i coords = ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY);
        currentObject.setPos(coords);
        isValid = currentObject.withinWorldBounds(); //&& !ElectricitySimulator.WORLD.occupiedAt(coords);
    }

    @Override
    public InputActionResult touchDown (int screenX, int screenY, int pointer, int button) {
        if (!isValid)
            return InputActionResult.failure(new InputManager.DefaultInputState());

        ElectricitySimulator.WORLD.addObject(currentObject);
        resetObjectAt(ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY), true);
        return InputActionResult.success(this);
    }

    @Override
    public InputActionResult keyDown (int code) {
        if (code == Input.Keys.UP || code == Input.Keys.DOWN) {
            currentIndex = MathUtils.clamp(
                    currentIndex + (code == Input.Keys.UP ? 1 : -1), 0, ALL_CREATORS.length - 1);
            resetObject();
            return InputActionResult.success(this);
        }

        if (code == Input.Keys.LEFT || code == Input.Keys.RIGHT) {
            currentObject.getProperties().rotateIfPossible(code == Input.Keys.LEFT);
            return InputActionResult.success(this);
        }

        return InputActionResult.failure(this);
    }

    private void resetObjectAt (Vector2i at, boolean withSameProperties) {
        WorldObject newObject = ALL_CREATORS[currentIndex].create(at);
        if (withSameProperties) newObject.getProperties().from(currentObject.getProperties());
        currentObject = newObject;
    }

    private void resetObject () {
        resetObjectAt(currentObject.getPos(), false);
    }
}
