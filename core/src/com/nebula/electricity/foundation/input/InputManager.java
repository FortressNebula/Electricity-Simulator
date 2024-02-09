package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.content.world.AllWorldObjects;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;
import java.util.UUID;

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
        Vector2i coords = ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenX, screenY);

        // Add cube
        Optional<UUID> optionalID = ElectricitySimulator.WORLD.objectAt(coords);

        if (optionalID.isPresent()) {
            ElectricitySimulator.WORLD.removeObject(optionalID.get());
        } else {
            ElectricitySimulator.WORLD.addObject(button == Input.Buttons.LEFT ?
                    AllWorldObjects.CUBE.create(coords) :
                    AllWorldObjects.CYLINDER.create(coords));
        }

        return true;
    }
}
