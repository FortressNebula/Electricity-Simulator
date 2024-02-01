package com.nebula.electricity.content.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.Module;

public class InputManager extends InputAdapter implements Module {

    @Override
    public void init () {
        Gdx.input.setInputProcessor(this);
    }

    @Override
    public boolean doesDraw () {
        return false;
    }

    @Override
    public void update () {
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
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        if (button != Input.Buttons.LEFT) return false;

        ElectricitySimulator.WORLD.selectedCoord = ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY);
        return true;
    }
}
