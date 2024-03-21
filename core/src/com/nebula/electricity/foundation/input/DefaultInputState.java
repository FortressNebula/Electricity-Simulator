package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;
import java.util.UUID;

class DefaultInputState extends InputState {

    public DefaultInputState () {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);
    }

    public DefaultInputState (Vector2i pos) {
        this();
    }

    @Override
    public boolean touchDown (Vector2i screenPos, int pointer, int button) {
        // Did we click on an object?
        Optional<UUID> id = ElectricitySimulator.WORLD.objectAt(
                ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenPos));

        if (id.isPresent()) {
            // Yes, let it know
            ElectricitySimulator.WORLD.getObject(id.get()).onClick(button == Input.Buttons.LEFT);
            return true;
        }

        return false;
    }

    @Override
    public boolean keyDown (int code) {
        return false;
    }
}
