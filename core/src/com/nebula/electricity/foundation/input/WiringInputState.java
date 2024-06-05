package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.Connection;
import com.nebula.electricity.foundation.electricity.ConnectionReference;
import com.nebula.electricity.foundation.electricity.Node;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.HashMap;
import java.util.Objects;

public class WiringInputState extends InputState {
    TextureAtlas.AtlasRegion openConnection;
    TextureAtlas.AtlasRegion closedConnection;
    HashMap<Integer, Node> allNodes;
    int connectingID;

    WiringInputState (Vector2i screenPos) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        // Initialise rendering tools
        openConnection = ElectricitySimulator.getGUITexture("open_connection");
        closedConnection = ElectricitySimulator.getGUITexture("closed_connection");

        // Keep track of all nodes to draw and track collisions for
        allNodes = new HashMap<>();
        ElectricitySimulator.WORLD.forEachObject(object -> {
            ElectricProperties props = object.getElectricProperties();
            if (Objects.isNull(props)) return;

            props.recheckConnected();
            for (Node n : props.getNodes())
                allNodes.put(n.getID(), n);
        });

        connectingID = -1;
    }

    void connect (Node n1, Node n2) {
        ElectricitySimulator.CIRCUIT_MANAGER.registerConnection(n1.getID(), n2.getID(), new Connection(0, 0));

        for (WorldObject object : ElectricitySimulator.WORLD.getAllObjects()) {
            ElectricProperties props = object.getElectricProperties();
            if (Objects.isNull(props))
                continue;

            boolean hasN1 = props.containsNode(n1.getID());
            boolean hasN2 = props.containsNode(n2.getID());

            if (hasN1 && hasN2) {
                // Cannot connect object to itself!
                ElectricitySimulator.CIRCUIT_MANAGER.removeConnection(n1.getID(), n2.getID());
                return;
            }

            if (!hasN1 && !hasN2)
                continue;

            if (!n1.getConnected() && hasN1) {
                n1.setConnected(true);
                props.markDirty();
            }

            if (!n2.getConnected() && hasN2) {
                n2.setConnected(true);
                props.markDirty();
            }

            props.update();
        }
    }

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        Vector2i unprojected = ElectricitySimulator.unproject(screenPos);
        Vector2i pos;
        Vector2i size = new Vector2i(Constants.SCALE * 8, Constants.SCALE * 9);

        for (Node node : allNodes.values()) {
            pos = node.getRenderPosition();
            if (unprojected.withinBounds(pos, pos.add(size))) {
                if (node.getConnected())
                    return false;
                if (!node.getEnabled())
                    return false;

                // Found the node we clicked on!!
                if (connectingID == -1) {
                    connectingID = node.getID();
                    return true;
                }

                // We already clicked on another node before, now we establish a connection
                connect(node, allNodes.get(connectingID));
                connectingID = -1;
            }
        }

        return false;
    }

    @Override
    boolean keyDown (int code) {
        return false;
    }

    @Override
    public void drawGUI (SpriteBatch batch) {
        batch.setProjectionMatrix(ElectricitySimulator.getCameraProjection());
        // Draw nodes
        for (Node node : allNodes.values()) {
            Vector2i drawPos = node.getRenderPosition();

            batch.setColor(1, 1, 1, node.getEnabled() ? 1 : 0.5f);
            batch.draw(node.getID() == connectingID || node.getConnected() ? closedConnection : openConnection,
                    drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
        }
        batch.setColor(1,1,1,1);
        batch.setProjectionMatrix(ElectricitySimulator.getGUIProjection());
    }

    @Override
    public void drawShapes (ShapeRenderer shapes) {
        // Draw connections
        shapes.setColor(0.5f,1f,0.2f,1);
        for (ConnectionReference ref : ElectricitySimulator.CIRCUIT_MANAGER.getAllConnections().keySet()) {
            //if (!ElectricitySimulator.CIRCUIT_MANAGER.getConnection(ref).shouldDraw)
            //    continue;

            Vector2i startPos = allNodes.get(ref.getID1()).getRenderPosition().add(20);
            Vector2i endPos   = allNodes.get(ref.getID2()).getRenderPosition().add(20);

            shapes.rectLine(startPos.x, startPos.y, endPos.x, endPos.y, 25);
        }
        shapes.setColor(1,1,1,1);
    }
}
