package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.math.Vector2i;

public class PanningInputState extends InputState {
    PanningInputState (Vector2i screenPos) {}

    @Override
    public void update () {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            ElectricitySimulator.getCamera().translate(0, 10);

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            ElectricitySimulator.getCamera().translate(0, -10);

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            ElectricitySimulator.getCamera().translate(-10, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            ElectricitySimulator.getCamera().translate(10, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.EQUALS))
            ElectricitySimulator.getCamera().zoom -= 0.1f;

        if (Gdx.input.isKeyPressed(Input.Keys.MINUS))
            ElectricitySimulator.getCamera().zoom += 0.1f;
    }

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        return false;
    }

    @Override
    boolean keyDown (int code) {
        if (Gdx.input.isKeyPressed(Input.Keys.R)) {
            ElectricitySimulator.getCamera().zoom = 1;
            ElectricitySimulator.getCamera().position.set(
                    Constants.SCALED_TILE_SIZE.x * Constants.WORLD_SIZE.x * 0.5f,
                    Constants.SCALED_TILE_SIZE.y * Constants.WORLD_SIZE.y * 0.5f,
                    0f);
        }

        return false;
    }
}
