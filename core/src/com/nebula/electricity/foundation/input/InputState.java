package com.nebula.electricity.foundation.input;

import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.math.Vector2i;

public abstract class InputState implements Module {
    @Override
    public void init () {}
    // Input methods
    abstract boolean touchDown (Vector2i screenPos, int pointer, int button);
    void mouseMoved (Vector2i screenPos) {}
    void mouseScrolled (float amount) {}
    abstract boolean keyDown (int code);
}
