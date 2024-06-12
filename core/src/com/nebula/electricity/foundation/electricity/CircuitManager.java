package com.nebula.electricity.foundation.electricity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.component.*;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import static com.nebula.electricity.ElectricitySimulator.CIRCUIT_MANAGER;

public class CircuitManager implements Module {
    public static int GLOBAL_VERTEX_ID = 0;

    public final ComponentManager<Integer, CircuitVertex> VERTICES;
    public final ComponentManager<ConnectionReference, Connection> CONNECTIONS;

    private final Color WIRE_COLOUR = Color.valueOf("5e6385");
    private final Color WIRE_SHADOW = Color.valueOf("282c43");

    public CircuitManager () {
        VERTICES = new ComponentManager<>();
        CONNECTIONS = new ComponentManager<>();

        VERTICES.addIDFunction(CircuitVertex::getID);
    }

    @Override
    public void init () {

    }

    @Override
    public void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {
        // Don't draw any of this in wiring mode
        if (ElectricitySimulator.INPUT_MANAGER.inWiringMode())
            return;

        ElectricitySimulator.setRenderModeAndStart(false, false);
        shapes.setColor(WIRE_COLOUR);
        drawVerticesWithYOffset(shapes, 0);
    }

    @Override
    public void draw (SpriteBatch batch, ShapeRenderer shapes) {
        // Don't draw any of this in wiring mode
        if (ElectricitySimulator.INPUT_MANAGER.inWiringMode())
            return;

        ElectricitySimulator.setRenderModeAndStart(false, false);
        shapes.setColor(WIRE_SHADOW);
        drawConnectionsWithYOffset(shapes, -16);
        drawVerticesWithYOffset(shapes, -16);
        shapes.setColor(WIRE_COLOUR);
        drawConnectionsWithYOffset(shapes, 0);
    }

    private void drawVerticesWithYOffset (ShapeRenderer shapes, int offset) {
        // Draw nodes
        for (Node node : CIRCUIT_MANAGER.VERTICES.getSubset(Node.class)) {
            if (!node.getConnected())
                continue;
            if (node.getDirection() == Direction.UP)
                continue;

            Vector2i drawPos = node.getCombinedPosition();

            shapes.rect(drawPos.x,
                    drawPos.y + offset - (node.getDirection() == Direction.DOWN ? 20 : 0),
                    Constants.SCALE * 8,
                    Constants.SCALE * (node.getDirection() == Direction.DOWN ? 12 : 8));
        }

        // Draw junctions
        for (Junction junction : CIRCUIT_MANAGER.VERTICES.getSubset(Junction.class)) {
            Vector2i drawPos = junction.getRenderPosition();

            shapes.rect(drawPos.x, drawPos.y + offset, Constants.SCALE * 8, Constants.SCALE * 8);
        }
    }

    private void drawConnectionsWithYOffset (ShapeRenderer shapes, int offset) {
        // Draw connections
        for (ConnectionReference ref : CIRCUIT_MANAGER.CONNECTIONS.getAllIDs()) {
            if (!CIRCUIT_MANAGER.CONNECTIONS.get(ref).shouldDraw)
                continue;

            Vector2i startPos = CIRCUIT_MANAGER.VERTICES.get(ref.getID1()).getCombinedPosition().add(20);
            Vector2i endPos   = CIRCUIT_MANAGER.VERTICES.get(ref.getID2()).getCombinedPosition().add(20);

            shapes.rectLine(startPos.x, startPos.y + offset, endPos.x, endPos.y + offset, 40);
        }
    }

    public void refreshConnectedNodes () {
        ElectricitySimulator.WORLD.forEachElectricalObject(object -> object.getElectricProperties().recheckConnected());
    }
}
