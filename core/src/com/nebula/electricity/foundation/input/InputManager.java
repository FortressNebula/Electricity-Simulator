package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.math.Vector2i;

import java.util.function.Function;

public class InputManager extends InputAdapter implements Module {
    // State-machine
    InputStates hoveringOverState;
    InputStates selectedState;
    InputState state;

    // Textures
    TextureAtlas.AtlasRegion guiBackground;
    TextureAtlas.AtlasRegion guiCorner;
    TextureAtlas.AtlasRegion hoverOverlay;

    @Override
    public void init () {
        Gdx.input.setInputProcessor(this);
        InputStates.register();

        state = new DefaultInputState();
        hoveringOverState = null;
        selectedState = InputStates.DEFAULT;

        guiBackground = ElectricitySimulator.getGUITexture("bg");
        guiCorner = ElectricitySimulator.getGUITexture("bg_corner");
        hoverOverlay = ElectricitySimulator.getGUITexture("hover_overlay");
    }

    @Override
    public void draw (SpriteBatch batch, Camera camera) {
        state.draw(batch, camera);
    }

    @Override
    public void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {
        // Draw the GUI
        // Background
        batch.draw(guiBackground, Gdx.graphics.getWidth() - InputStates.values().length*120, 0,
                InputStates.values().length * 120, 120);
        batch.draw(guiCorner, Gdx.graphics.getWidth() - InputStates.values().length*120 - 120, 0,
                120, 120);
        // Buttons
        for (InputStates state : InputStates.values()) {
            batch.draw(state.region, Gdx.graphics.getWidth() - state.position.x, state.position.y,
                    80, 80);

            if (state == selectedState) {
                // Draw select overlay
                batch.setColor(1, 1, 1, 0.3f);
                batch.draw(hoverOverlay, Gdx.graphics.getWidth() - state.position.x - 10, state.position.y - 10,
                        100, 100);
                batch.setColor(1, 1, 1, 1);
                continue;
            }

            if (state == hoveringOverState) {
                // Draw hover overlay
                batch.setColor(1, 1, 1, Gdx.input.isButtonPressed(Input.Buttons.LEFT) ? 0.5f : 0.2f);
                batch.draw(hoverOverlay, Gdx.graphics.getWidth() - state.position.x - 10, state.position.y - 10,
                        100, 100);
                batch.setColor(1, 1, 1, 1);
            }
        }

        state.drawGUI(batch, shapes);
    }

    @Override
    public void update () {
        state.update();
    }

    @Override
    public boolean touchDown (int screenX, int screenY, int pointer, int button) {
        // Button mechanics
        if (hoveringOverState != null) {
            selectedState = hoveringOverState;
            state = selectedState.create(screenX, screenY);
        }

        return state.touchDown(new Vector2i(screenX, screenY), pointer, button);
    }

    @Override
    public boolean touchDragged (int screenX, int screenY, int pointer) {
        state.touchDragged(new Vector2i(screenX, screenY), pointer);
        return false;
    }

    @Override
    public boolean touchUp (int screenX, int screenY, int pointer, int button) {
        return state.touchUp(new Vector2i(screenX, screenY), pointer, button);
    }

    @Override
    public boolean mouseMoved (int screenX, int screenY) {
        Vector2i pos = new Vector2i(screenX, screenY);
        state.mouseMoved(pos);

        // Button mechanics
        pos.x = Gdx.graphics.getWidth() - pos.x;
        pos.y = Gdx.graphics.getHeight() - pos.y;

        if (pos.y > 120) {
            hoveringOverState = null;
            return false;
        }

        for (InputStates state : InputStates.values()) {
            if (pos.withinBounds(state.position.x - 90, state.position.y - 10, state.position.x + 10, state.position.y + 90)) {
                hoveringOverState = state;
                return false;
            }
        }

        hoveringOverState = null;
        return false;
    }

    @Override
    public boolean keyDown (int keycode) {
        return state.keyDown(keycode);
    }

    @Override
    public boolean scrolled (float amountX, float amountY) {
        state.mouseScrolled(amountY);
        return false;
    }

    public enum InputStates {
        PANNING("panning", PanningInputState::new),
        WIRING ( "wiring",  WiringInputState::new),
        PLACING("placing", PlacingInputState::new),
        DEFAULT("default", DefaultInputState::new)
        ;
        final String textureName;
        final Function<Vector2i, InputState> factory;
        TextureAtlas.AtlasRegion region;
        // Collision bounding box
        Vector2i position;

        InputStates (String textureName, Function<Vector2i, InputState> factory) {
            this.textureName = textureName;
            this.factory = factory;
        }

        private void init (int selfIndex) {
            region = ElectricitySimulator.getGUITexture(textureName);
            position = new Vector2i(120 * selfIndex + 100, 20);
        }

        InputState create (int screenX, int screenY) {
            return factory.apply(new Vector2i(screenX, screenY));
        }

        private static void register () {
            for (int i = 0; i < values().length; i++) {
                values()[i].init(i);
            }
        }
    }
}
