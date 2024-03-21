package com.nebula.electricity.foundation.input;

import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.math.Vector2i;

public abstract class InputState implements Module {
    @Override
    public void init () {}

    // Input methods
    // Required
    abstract boolean touchDown (Vector2i screenPos, int pointer, int button);
    abstract boolean keyDown (int code);

    // Optional overrides
    boolean touchUp (Vector2i screenPos, int pointer, int button) { return false; }
    void touchDragged (Vector2i screenPos, int pointer) {}
    void mouseMoved (Vector2i screenPos) {}
    void mouseScrolled (float amount) {}
}
