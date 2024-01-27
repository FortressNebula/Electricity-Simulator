package com.nebula.electricity.content.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Module;

public class InputManager implements Module {

    public void init () {

    }

    @Override
    public boolean doesDraw () {
        return false;
    }

    @Override
    public void update () {
        if (Gdx.input.isKeyPressed(Input.Keys.W))
            ElectricitySimulator.cameraTranslate(0, 10);

        if (Gdx.input.isKeyPressed(Input.Keys.S))
            ElectricitySimulator.cameraTranslate(0, -10);

        if (Gdx.input.isKeyPressed(Input.Keys.A))
            ElectricitySimulator.cameraTranslate(-10, 0);

        if (Gdx.input.isKeyPressed(Input.Keys.D))
            ElectricitySimulator.cameraTranslate(10, 0);
    }
}
