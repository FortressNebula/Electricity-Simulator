package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.*;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import static com.nebula.electricity.ElectricitySimulator.CIRCUIT_MANAGER;

public class WiringInputState extends InputState {
    TextureAtlas.AtlasRegion openConnection;
    TextureAtlas.AtlasRegion closedConnection;
    TextureAtlas.AtlasRegion junctionConnection;
    TextureAtlas.AtlasRegion selectedJunctionConnection;
    int connectingID;

    WiringInputState (Vector2i screenPos) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        // Initialise rendering tools
        openConnection = ElectricitySimulator.getGUITexture("open_connection");
        closedConnection = ElectricitySimulator.getGUITexture("closed_connection");
        junctionConnection = ElectricitySimulator.getGUITexture("junction_connection");
        selectedJunctionConnection = ElectricitySimulator.getGUITexture("selected_junction");

        CIRCUIT_MANAGER.refreshConnectedNodes();

        connectingID = -1;
    }

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        Vector2i unprojected = ElectricitySimulator.unproject(screenPos);
        Vector2i pos;
        Vector2i size = new Vector2i(Constants.SCALE * 8, Constants.SCALE * 9);

        for (CircuitVertex vertex : CIRCUIT_MANAGER.getVertices()) {
            pos = vertex.getRenderPosition();
            if (!unprojected.withinBounds(pos, pos.add(size)))
                continue;

            if (button == Input.Buttons.LEFT)
                return connect(vertex);
            if (button == Input.Buttons.RIGHT)
                return disconnect(vertex, false);
            if (button == Input.Buttons.MIDDLE && vertex instanceof Junction)
                return disconnect(vertex, true);
        }

        if (button == Input.Buttons.MIDDLE) {
            Vector2i coords = ElectricitySimulator.WORLD.coordinatesFromScreenPos(screenPos);

            // Is there already a junction here?
            for (Junction junction : CIRCUIT_MANAGER.getJunctions()) {
                if (junction.getPosition().equals(coords)) {
                    return false;
                }
            }

            // Is there an object here?
            if (ElectricitySimulator.WORLD.occupiedAt(coords))
                return false;

            // New Junction!
            CIRCUIT_MANAGER.registerVertex(new Junction(coords));
            return true;
        }

        connectingID = -1;
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
        for (Node node : CIRCUIT_MANAGER.getNodes()) {
            Vector2i drawPos = node.getRenderPosition();

            batch.setColor(1, 1, 1, node.getEnabled() ? 1 : 0.5f);
            batch.draw(node.getID() == connectingID || node.getConnected() ? closedConnection : openConnection,
                    drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
        }
        // Draw junctions
        batch.setColor(1,1,1,1);
        for (Junction junction : CIRCUIT_MANAGER.getJunctions()) {
            Vector2i drawPos = junction.getRenderPosition();

            batch.draw(junction.getID() == connectingID ? selectedJunctionConnection : junctionConnection,
                    drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
        }
        batch.setProjectionMatrix(ElectricitySimulator.getGUIProjection());
    }

    @Override
    public void drawShapes (ShapeRenderer shapes) {
        // Draw connections
        shapes.setColor(0.5f,1f,0.2f,1);
        for (ConnectionReference ref : CIRCUIT_MANAGER.getAllConnections()) {
            //if (!CIRCUIT_MANAGER.getConnection(ref).shouldDraw)
            //    continue;

            Vector2i startPos = CIRCUIT_MANAGER.getVertex(ref.getID1()).getRenderPosition().add(20);
            Vector2i endPos   = CIRCUIT_MANAGER.getVertex(ref.getID2()).getRenderPosition().add(20);

            shapes.rectLine(startPos.x, startPos.y, endPos.x, endPos.y, 25);
        }
        shapes.setColor(1,1,1,1);
    }

    //region Connection

    boolean connect (CircuitVertex vertex) {
        // Select new node
        if (connectingID == -1 && vertex.canConnect()) {
            connectingID = vertex.getID();
            return false;
        }

        // Node to junction connection
        if (vertex instanceof Node != CIRCUIT_MANAGER.getVertex(connectingID) instanceof Node)
            return vertex instanceof Node ? connectNodeToJunction((Node) vertex, CIRCUIT_MANAGER.getJunction(connectingID))
                    : connectNodeToJunction(CIRCUIT_MANAGER.getNode(connectingID), (Junction) vertex);

        // Junction to junction connection
        if (!(vertex instanceof Node)) {
            CIRCUIT_MANAGER.registerConnection(vertex.getID(), connectingID,Connection.external());
            connectingID = -1;
            return true;
        }

        // Node to node connection
        return connectNodeToNode((Node) vertex, CIRCUIT_MANAGER.getNode(connectingID));
    }

    boolean connectNodeToNode (Node a, Node b) {
        if (!a.canConnect() || !b.canConnect())
            return false;

        // We already clicked on another node before, now we establish a connection
        CIRCUIT_MANAGER.registerConnection(a.getID(), b.getID(), Connection.external());

        for (WorldObject object : ElectricitySimulator.WORLD.getAllObjects()) {
            if (!object.isElectric())
                continue;

            ElectricProperties props = object.getElectricProperties();

            boolean hasA = props.containsNode(a.getID());
            boolean hasB = props.containsNode(b.getID());

            if (hasA == hasB) {
                if (!hasA) // Object contains neither node, skip
                    continue;

                // Cannot connect object to itself!
                CIRCUIT_MANAGER.deleteConnection(a.getID(), b.getID());
                return false;
            }

            a.setConnected(a.getConnected() || hasA);
            b.setConnected(b.getConnected() || hasB);

            props.update();
        }

        connectingID = -1;
        return true;
    }

    boolean connectNodeToJunction (Node n, Junction j) {
        if (!n.canConnect())
            return false;

        // We already clicked on a junction before, now we establish a connection
        CIRCUIT_MANAGER.registerConnection(n.getID(), j.getID(), Connection.external());

        n.setConnected(true);
        ElectricitySimulator.WORLD.forEachElectricalObject(object -> {
            if (object.getElectricProperties().containsNode(n.getID()))
                object.getElectricProperties().update();
        });

        connectingID = -1;
        return true;
    }

    //endregion

    boolean disconnect (CircuitVertex vertex, boolean shouldDelete) {
        if (vertex instanceof Node)
            ((Node) vertex).setConnected(false);
        if (shouldDelete)
            CIRCUIT_MANAGER.deleteVertex(vertex);

        for (ConnectionReference ref : CIRCUIT_MANAGER.getAllConnections()) {
            if (ref.containsNode(vertex.getID()) && !CIRCUIT_MANAGER.getConnection(ref).isInternal)
                CIRCUIT_MANAGER.queueDeleteConnection(ref);
        }

        CIRCUIT_MANAGER.flushDeletionQueue();
        ElectricitySimulator.WORLD.forEachElectricalObject(object -> object.getElectricProperties().recheckConnected());

        return true;
    }
}
