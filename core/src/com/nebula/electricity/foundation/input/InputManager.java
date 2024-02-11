package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;

public class InputManager extends InputAdapter implements Module {
    InputState state;

    @Override
    public void init () {
        state = new DefaultInputState();
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean doesDraw () {
        return true;
    }

    @Override
    public void draw (SpriteBatch batch, Camera camera) {
        state.draw(batch, camera);
    }

    @Override
    public void update () {
        state.update();
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        InputActionResult result = state.touchDown(screenX, screenY, pointer, button);
        state = result.getNewMode();
        return result.wasSuccessful();
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        state.mouseMoved(screenX, screenY);
        return false;
    }

    @Override
    public boolean keyDown (int keycode) {
        InputActionResult result = state.keyDown(keycode);
        state = result.getNewMode();
        return result.wasSuccessful();
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        state.mouseScrolled(amountY);
        return false;
    }

    static class DefaultInputState implements InputState {

        public DefaultInputState () {
            Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
        }

        @Override
        public InputActionResult touchDown (int screenX, int screenY, int pointer, int button) {
            if (ElectricitySimulator.WORLD.isScreenPosInWorld(screenX, screenY))
                return InputActionResult.success(new PlacingInputState(screenX, screenY));
            return InputActionResult.failure(this);
        }

        @Override
        public InputActionResult keyDown (int code) {
            // TODO: fix when we actually want the camera moving again
//        if (Gdx.input.isKeyPressed(Input.Keys.W))
//            ElectricitySimulator.cameraTranslate(0, 10);
//
//        if (Gdx.input.isKeyPressed(Input.Keys.S))
//            ElectricitySimulator.cameraTranslate(0, -10);
//
//        if (Gdx.input.isKeyPressed(Input.Keys.A))
//            ElectricitySimulator.cameraTranslate(-10, 0);
//
//        if (Gdx.input.isKeyPressed(Input.Keys.D))
//            ElectricitySimulator.cameraTranslate(10, 0);
//
//        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
//            ElectricitySimulator.cameraZoom(0.1f);
//
//        if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
//            ElectricitySimulator.cameraZoom(-0.1f);

            return InputActionResult.failure(this);
        }
    }
}
