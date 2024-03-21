package com.nebula.electricity.foundation.input;

import com.nebula.electricity.math.Vector2i;

public class WiringInputState extends InputState {
    WiringInputState (Vector2i screenPos) {}

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        return false;
    }

    @Override
    boolean keyDown (int code) {
        return false;
    }
}
