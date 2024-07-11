package com.nebula.electricity.foundation.input;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.electricity.circuit.Circuit;
import com.nebula.electricity.foundation.electricity.component.*;
import com.nebula.electricity.foundation.world.object.ElectricProperties;
import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.math.Vector2i;

import java.util.Collection;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class WiringInputState extends InputState {
    TextureAtlas.AtlasRegion openConnection;
    TextureAtlas.AtlasRegion closedConnection;
    TextureAtlas.AtlasRegion junctionConnection;
    TextureAtlas.AtlasRegion selectedJunctionConnection;
    int connectingID;

    // DEBUG
    private DebugMode debugMode = DebugMode.OFF;
    private int cycleID = 0;
    private final BitmapFont debugFont;

    WiringInputState (Vector2i screenPos) {
        Gdx.graphics.setSystemCursor(Cursor.SystemCursor.Arrow);

        // Initialise rendering tools
        openConnection = ElectricitySimulator.getGUITexture("open_connection");
        closedConnection = ElectricitySimulator.getGUITexture("closed_connection");
        junctionConnection = ElectricitySimulator.getGUITexture("junction_connection");
        selectedJunctionConnection = ElectricitySimulator.getGUITexture("selected_junction");

        connectingID = -1;

        // DEBUG
        debugFont = new BitmapFont();
    }

    @Override
    public void dispose () {
        debugFont.dispose();
    }

    @Override
    boolean touchDown (Vector2i screenPos, int pointer, int button) {
        Vector2i unprojected = ElectricitySimulator.unproject(screenPos);
        Vector2i pos;
        Vector2i size = new Vector2i(Constants.SCALE * 8, Constants.SCALE * 9);

        for (CircuitVertex vertex : ELECTRICITY.VERTICES.getAll()) {
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
            for (Junction junction : ELECTRICITY.VERTICES.getSubset(Junction.class)) {
                if (junction.getPosition().equals(coords)) {
                    return false;
                }
            }

            // Is there an object here?
            if (ElectricitySimulator.WORLD.occupiedAt(coords))
                return false;

            // New Junction!
            ELECTRICITY.VERTICES.add(new Junction(coords));
            return true;
        }

        connectingID = -1;
        return false;
    }

    @Override
    boolean keyDown (int code) {
        if (code == Input.Keys.D)
            debugMode = debugMode.next();

        if (debugMode != DebugMode.CYCLES)
            return false;

        if (code == Input.Keys.UP)
            cycleID++;

        if (code == Input.Keys.DOWN)
            cycleID--;

        System.out.println("Cycle ID is " + cycleID);

        return true;
    }

    @Override
    public void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {
        ElectricitySimulator.setRenderModeAndStart(true, false);

        // Draw nodes
        for (Node node : ELECTRICITY.VERTICES.getSubset(Node.class)) {
            Vector2i drawPos = node.getRenderPosition();

            batch.setColor(1, 1, 1, node.getEnabled() ? 1 : 0.5f);
            batch.draw(node.getID() == connectingID || node.getConnected() ? closedConnection : openConnection,
                    drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
        }
        // Draw junctions
        batch.setColor(1,1,1,1);
        for (Junction junction : ELECTRICITY.VERTICES.getSubset(Junction.class)) {
            Vector2i drawPos = junction.getRenderPosition();

            batch.draw(junction.getID() == connectingID ? selectedJunctionConnection : junctionConnection,
                    drawPos.x, drawPos.y, Constants.SCALE * 8, Constants.SCALE * 9);
        }

        // Draw debug text
        if (debugMode == DebugMode.OFF)
            return;

        ElectricitySimulator.setRenderModeAndStart(true, true);
        debugFont.draw(batch, debugMode.name(), 10, 30);
    }

    @Override
    public void draw (SpriteBatch batch, ShapeRenderer shapes) {
        ElectricitySimulator.setRenderModeAndStart(false, false);

        // Draw circuits
        for (Circuit circuit : ELECTRICITY.CIRCUITS) {
            shapes.setColor(circuit.getDebugColour());

            if (debugMode == DebugMode.OFF) {
                drawConnections(circuit.getConnections(), shapes, false);
            } else if (debugMode == DebugMode.CYCLES) {
                if (cycleID >= circuit.getCycles().size() || cycleID < 0)
                    continue;

                drawConnections(circuit.getCycles().get(cycleID).getConnections(), shapes, true);
            } else {
                drawConnections(circuit.getSpanningTree(), shapes, true);
            }

        }
        shapes.setColor(1,1,1,1);
    }

    void drawConnections (Collection<Connection> all, ShapeRenderer shapes, boolean drawInternals) {
        for (Connection ref : all) {
            //if (!ELECTRICITY.CONNECTIONS.get(ref).shouldDraw && !drawInternals)
            //    continue;

            Vector2i startPos = ELECTRICITY.VERTICES.get(ref.getID1()).getRenderPosition().add(20);
            Vector2i endPos = ELECTRICITY.VERTICES.get(ref.getID2()).getRenderPosition().add(20);

            shapes.rectLine(startPos.x, startPos.y, endPos.x, endPos.y, 25);
        }
    }

    //region Connection

    boolean connect (CircuitVertex vertex) {
        // Select new node
        if (connectingID == -1 && vertex.canConnect()) {
            connectingID = vertex.getID();
            return false;
        }

        // Node to junction connection
        if (vertex instanceof Node != ELECTRICITY.VERTICES.get(connectingID) instanceof Node)
            return vertex instanceof Node ?
                    connectNodeToJunction((Node) vertex, (Junction) ELECTRICITY.VERTICES.get(connectingID)) :
                    connectNodeToJunction((Node) ELECTRICITY.VERTICES.get(connectingID), (Junction) vertex);

        // Junction to junction connection
        if (!(vertex instanceof Node)) {
            ELECTRICITY.CONNECTIONS.add(Connection.of(vertex.getID(), connectingID), ConnectionData.external());
            vertex.setConnected(true);
            ELECTRICITY.VERTICES.get(connectingID).setConnected(true);
            connectingID = -1;
            return true;
        }

        // Node to node connection
        return connectNodeToNode((Node) vertex, (Node) ELECTRICITY.VERTICES.get(connectingID));
    }

    boolean connectNodeToNode (Node a, Node b) {
        if (!a.canConnect() || !b.canConnect())
            return false;

        // We already clicked on another node before, now we establish a connection
        ELECTRICITY.CONNECTIONS.add(Connection.of(a, b), ConnectionData.external());

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
                ELECTRICITY.CONNECTIONS.delete(Connection.of(a,b));
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
        ELECTRICITY.CONNECTIONS.add(Connection.of(n,j), ConnectionData.external());

        n.setConnected(true);
        j.setConnected(true);

        ElectricitySimulator.WORLD.forEachElectricalObject(object -> {
            if (object.getElectricProperties().containsNode(n.getID()))
                object.getElectricProperties().update();
        });

        connectingID = -1;
        return true;
    }

    //endregion

    boolean disconnect (CircuitVertex vertex, boolean shouldDelete) {
        vertex.setConnected(false);

        if (shouldDelete)
            ELECTRICITY.VERTICES.delete(vertex.getID());

        for (Connection ref : ELECTRICITY.CONNECTIONS.getAllIDs()) {
            if (ref.containsVertex(vertex.getID()) && !ELECTRICITY.CONNECTIONS.get(ref).isInternal)
                ELECTRICITY.CONNECTIONS.queueDelete(ref);
        }
        ELECTRICITY.CONNECTIONS.flush();

        ELECTRICITY.refreshConnected();

        return true;
    }

    enum DebugMode {
        OFF,
        CYCLES,
        SPANNING_TREE;

        DebugMode next () {
            int i = ordinal() + 1;
            return i == values().length ? values()[0] : values()[i];
        }
    }
}
