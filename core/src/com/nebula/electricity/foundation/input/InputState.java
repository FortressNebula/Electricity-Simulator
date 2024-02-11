package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public interface InputState {
    InputActionResult touchDown (int screenX, int screenY, int pointer, int button);
    default void mouseMoved (int screenX, int screenY) {}
    default void mouseScrolled (float amount) {}
    InputActionResult keyDown (int code);

    default void update () {}
    default void draw (SpriteBatch batch, Camera camera) {}
}
