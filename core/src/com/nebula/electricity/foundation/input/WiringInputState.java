package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.Connection;
import com.nebula.electricity.foundation.electricity.ElectricObjectHandler;
import com.nebula.electricity.math.Vector2i;

import java.util.Optional;

public class WiringInputState extends InputState {
    TextureAtlas.AtlasRegion openConnection;

    WiringInputState (Vector2i screenPos) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        openConnection = ElectricitySimulator.getGUITexture("open_connection");
    }

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        return false;
    }

    @Override
    boolean keyDown (int code) {
        return false;
    }

    @Override
    public void draw (SpriteBatch batch) {
        ElectricitySimulator.WORLD.forEachObject(object -> {
            Optional<ElectricObjectHandler> handler = object.getElectricHandler();
            if (!handler.isPresent()) return;

            for (Connection connection : handler.get().getConnections()) {
                Vector2i drawPos = connection.getRenderPosition();
                batch.draw(openConnection, drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
            }
        });
    }
}
