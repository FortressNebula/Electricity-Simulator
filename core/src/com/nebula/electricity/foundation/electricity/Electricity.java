package com.nebula.electricity.foundation.electricity;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Constants;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.circuit.Circuit;
import com.nebula.electricity.foundation.electricity.component.*;
import com.nebula.electricity.foundation.events.Events;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class Electricity implements Module {
    public static int GLOBAL_VERTEX_ID = 0;

    public final Registry<Integer, CircuitVertex> VERTICES;
    public final Registry<Connection, ConnectionData> CONNECTIONS;

    public final List<Circuit> CIRCUITS;

    private final Color WIRE_COLOUR = Color.valueOf("5e6385");
    private final Color WIRE_SHADOW = Color.valueOf("282c43");

    public Electricity () {
        VERTICES = new Registry<>() {
            @Override
            Integer makeID (CircuitVertex component) { return component.getID(); }
        };

        CONNECTIONS = new Registry<>() {
            @Override
            Connection makeID (ConnectionData component) {
                throw new IllegalStateException("what on earth are you doing");
            }

            @Override
            void onAdded (Connection id) {
                mergeOrCreateCircuit(id);
            }

            @Override
            void onDeleted (Connection id) {
                splitOrDeleteCircuit(id);
            }

            @Override
            void onClear () { CIRCUITS.clear(); }
        };

        CIRCUITS = new ArrayList<>();
    }

    @Override
    public void init () {}

    public void refreshConnected () {
        VERTICES.all.forEach((integer, circuitVertex) -> circuitVertex.setConnected(false));

        for (Connection ref : CONNECTIONS.getAllIDs()) {
            // Discard internal connections
            if (ELECTRICITY.CONNECTIONS.get(ref).isInternal)
                continue;

            VERTICES.get(ref.getID1()).setConnected(true);
            VERTICES.get(ref.getID2()).setConnected(true);
        }

        ElectricitySimulator.WORLD.forEachElectricalObject(object -> object.getElectricProperties().recheckInternalConnection());
    }

    void mergeOrCreateCircuit (Connection ref) {
        Optional<Integer> index1 = getCircuitIndex(ref.getID1());
        Optional<Integer> index2 = getCircuitIndex(ref.getID2());

        if (!(index1.isPresent() || index2.isPresent())) {
            // Neither vertex has a circuit, so create a new one
            CIRCUITS.add(Circuit.make(ref));
        } else if (index1.isPresent() != index2.isPresent()) {
            // One of the vertices has no circuit, simply amalgamate
            CIRCUITS.get(index1.orElseGet(index2::get)).include(ref);
        } else {
            // Both vertices have a circuit. They are either the same, or different.
            int a = index1.get();
            int b = index2.get();

            CIRCUITS.get(a).include(ref); // Either way, add the connection to the first one

            if (a != b) {
                // They are different, so merge the second circuit too
                CIRCUITS.get(a).merge(CIRCUITS.get(b));
                CIRCUITS.remove(b);
            }
        }
    }

    void splitOrDeleteCircuit (Connection ref) {
        Optional<Integer> optionalIndex = getCircuitIndex(ref.getID1());
        if (!optionalIndex.isPresent())
            return;
        int i = optionalIndex.get();

        Circuit circuit = CIRCUITS.get(i);
        circuit.exclude(ref);

        // Delete if empty
        if (circuit.isEmpty())
            CIRCUITS.remove(i);
        else {
            // It is not empty. Check if we have split the graph apart into sub-circuits
            Circuit subgraph = Circuit.make().include(getSubgraphFrom(ref.getID1()));

            if (subgraph.containsVertex(ref.getID2()))
                return; // It has *not* split the graph apart

            // It *has* split the graph apart
            subgraph.setDebugColour(CIRCUITS.get(i).getDebugColour());
            CIRCUITS.remove(i);

            CIRCUITS.add(subgraph);
            CIRCUITS.add(Circuit.make().include(getSubgraphFrom(ref.getID2())));
        }
    }

    Optional<Integer> getCircuitIndex (int vertexID) {
        for (int i = 0; i < CIRCUITS.size(); i++) {
            if (CIRCUITS.get(i).containsVertex(vertexID))
                return Optional.of(i);
        }
        return Optional.empty();
    }

    List<Connection> getSubgraphFrom (int start) {
        List<Connection> visited = new ArrayList<>();
        Deque<Integer> stack = new ArrayDeque<>();
        stack.push(start);

        // Iterative DFS approach
        while (!stack.isEmpty()) {
            int i = stack.pop();

            // Check all connections that feature this vertex and have NOT been visited
            List<Connection> adjacent = CONNECTIONS.getAllIDs().stream()
                    .filter(ref -> ref.containsVertex(i))
                    .filter(ref -> !visited.contains(ref))
                    .collect(Collectors.toList());

            for (Connection ref : adjacent) {
                // Visit each connection
                visited.add(ref);

                stack.push(ref.getOther(i));
            }
        }

        return visited;
    }

    //region Rendering

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
        for (Node node : ELECTRICITY.VERTICES.getSubset(Node.class)) {
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
        for (Junction junction : ELECTRICITY.VERTICES.getSubset(Junction.class)) {
            Vector2i drawPos = junction.getRenderPosition();

            shapes.rect(drawPos.x, drawPos.y + offset, Constants.SCALE * 8, Constants.SCALE * 8);
        }
    }

    private void drawConnectionsWithYOffset (ShapeRenderer shapes, int offset) {
        // Draw connections
        for (Connection ref : ELECTRICITY.CONNECTIONS.getAllIDs()) {
            if (!ELECTRICITY.CONNECTIONS.get(ref).shouldDraw)
                continue;

            Vector2i startPos = ELECTRICITY.VERTICES.get(ref.getID1()).getCombinedPosition().add(20);
            Vector2i endPos   = ELECTRICITY.VERTICES.get(ref.getID2()).getCombinedPosition().add(20);

            shapes.rectLine(startPos.x, startPos.y + offset, endPos.x, endPos.y + offset, 40);
        }
    }

    //endregion
}
