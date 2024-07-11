package com.nebula.electricity.foundation.explosion;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.math.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CircuitExplosionManager implements Module {
    float explosionTicks;
    final float explosionFlashDuration = 100f;

    // Bit of a hack
    TextureAtlas.AtlasRegion explosionGlow;

    @Override
    public void init () {
        explosionTicks = 0;

        explosionGlow = ElectricitySimulator.getGUITexture("hover_overlay");
    }

    public void checkExplosions () {
        List<UUID> toDelete = new ArrayList<>();

        ElectricitySimulator.WORLD.forEachElectricalObject(object -> {
            ElectricProperties properties = object.getElectricProperties();

            if (!properties.getInternalID().isPresent())
                return;

            Connection id = properties.getInternalID().get();

            if (ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(id) == null)
                return;

            if (ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(id).getCurrent() > properties.getMaxCurrent()) {
                explosionTicks = explosionFlashDuration;
                toDelete.add(ElectricitySimulator.WORLD.idOf(object).get());
                spawnFireAround(object.getPos());
            }
        });

        toDelete.forEach(ElectricitySimulator.WORLD::removeObject);
    }

    public void spawnFireAround (Vector2i pos) {
        for (int i = 0; i < 5; i++) {
            int randomDegrees = ElectricitySimulator.RANDOM.nextInt(0, 360);
            int randomRadius = ElectricitySimulator.RANDOM.nextInt(2, 5);

            int offsetX = (int) (MathUtils.cosDeg(randomDegrees) * randomRadius);
            int offsetY = (int) (MathUtils.sinDeg(randomDegrees) * randomRadius);

            ElectricitySimulator.WORLD.setFireAt(pos.add(offsetX, offsetY));
        }
    }


    @Override
    public void update () {
        if (explosionTicks > 0) explosionTicks--;
        checkExplosions();
    }

    @Override
    public void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {
        if (explosionTicks == 0)
            return;

        ElectricitySimulator.setRenderModeAndStart(true, true);
        batch.setColor(1,1,0.9f,explosionTicks / explosionFlashDuration * 2);
        batch.draw(explosionGlow, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.setColor(1,1,1,1);
    }
}
