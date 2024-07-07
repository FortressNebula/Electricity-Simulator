package com.nebula.electricity.foundation.electricity.circuit;

import com.badlogic.gdx.graphics.Color;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.Connection;

import java.util.*;
import java.util.stream.Collectors;

public class Circuit {
    Color debugColour;

    List<FundamentalCycle> fundamentalCycles;
    HashMap<Connection, CircuitDirection> connections; // Sorted by direction

    private Circuit () {
        debugColour = new Color(ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                1f);
        fundamentalCycles = new ArrayList<>();
        connections = new HashMap<>();
    }

    public static Circuit make () {
        return new Circuit();
    }

    public static Circuit make (Connection first) {
        return new Circuit().include(first);
    }

    public boolean containsVertex (int id) {
        for (Connection ref : connections.keySet())
            if (ref.containsVertex(id))
                return true;

        return false;
    }

    public Circuit include (Connection ref) {
        connections.put(ref, CircuitDirection.UNDIRECTED);
        update();
        return this;
    }

    public Circuit include (Collection<Connection> refs) {
        for (Connection ref : refs)
            connections.put(ref, CircuitDirection.UNDIRECTED);
        update();
        return this;
    }

    public Circuit exclude (Connection ref) {
        connections.remove(ref);
        update();
        return this;
    }

    public boolean isEmpty () {
        return connections.keySet().isEmpty();
    }

    public void merge (Circuit other) {
        include(other.connections.keySet());
    }

    // Using the newly-collected array of connections, update the fundamental cycles
    public void update () {
        List<Connection> spanningTree = getSpanningTree();
        if (spanningTree.isEmpty())
            return;

        fundamentalCycles.clear();
        // Make all connections undirected
        connections.keySet().forEach(ref -> connections.put(ref, CircuitDirection.UNDIRECTED));

        List<Connection> removedConnections = connections.keySet().stream()
                .filter(ref -> !spanningTree.contains(ref))
                .collect(Collectors.toList());

        for (Connection ref : removedConnections) {
            // Generate a fundamental cycle
            spanningTree.add(ref);
            fundamentalCycles.add(generateCycle(spanningTree, ref));
            spanningTree.remove(ref);
        }
    }

    FundamentalCycle generateCycle (List<Connection> all, Connection removed) {
        List<Integer> nodes = new FundamentalCycle.Pruner(all).prune(removed);
        List<Connection> cycleConnections = new ArrayList<>();

        if (nodes.size() == 0)
            return new FundamentalCycle(List.of(), false);

        nodes.add(nodes.get(0)); // Just to make it easier to loop around

        CircuitDirection direction = CircuitDirection.UNDIRECTED;

        // Iterate over every connection
        for (int i = 0; i < nodes.size() - 1; i++) {
            // Check its direction
            int from = nodes.get(i);
            int to = nodes.get(i + 1);

            CircuitDirection connectionDirection =
                    connections.get(Connection.of(from, to)).relativeTo(from, to);

            if (connectionDirection == CircuitDirection.UNDIRECTED) // Doesn't really matter if it just justifies what we've already seen
                continue;
            // We found an oriented connection that's different!!

            if (direction == CircuitDirection.UNDIRECTED) // Set the cycle direction
                direction = connectionDirection;
            else if (connectionDirection != direction) {
                // wuh oh, that's a conflicting loop
                direction = CircuitDirection.INVALID;
                break;
            }
        }

        if (direction == CircuitDirection.UNDIRECTED)
            direction = CircuitDirection.FORWARD;

        for (int i = 0; i < nodes.size() - 1; i++) {
            int from = nodes.get(i);
            int to = nodes.get(i + 1);

            if (direction != CircuitDirection.INVALID)
                connections.put(Connection.of(from, to), direction.relativeTo(from, to));
            cycleConnections.add(Connection.of(from, to));
        }

        return new FundamentalCycle(cycleConnections, direction != CircuitDirection.INVALID);
    }

    public List<FundamentalCycle> getCycles () { return fundamentalCycles; }
    public Set<Connection> getConnections () { return connections.keySet(); }
    public HashMap<Connection, CircuitDirection> getConnectionMap () { return connections; }

    public Color getDebugColour () { return debugColour; }
    public void setDebugColour (Color colour) { debugColour = colour; }

    // Solving mechanics

    public List<Connection> getSpanningTree () {
        if (connections.keySet().isEmpty())
            return List.of();

        List<Integer> visited = new ArrayList<>();
        List<Connection> spanningTree = new ArrayList<>();

        ArrayDeque<Integer> stack = new ArrayDeque<>();
        // It shouldn't be this hard to just get one item out of a set jesus christ
        stack.add(new ArrayList<>(connections.keySet()).get(0).getID1());

        // DFS woohoo
        while (!stack.isEmpty()) {
            int i = stack.pop();

            // Check all connections that feature this vertex and have NOT been visited
            List<Connection> adjacent = connections.keySet().stream()
                    .filter(ref -> ref.containsVertex(i))
                    .filter(ref -> !visited.contains(ref.getOther(i)))
                    .filter(ref -> !spanningTree.contains(ref))
                    .collect(Collectors.toList());

            for (Connection ref : adjacent) {
                // Visit each connection
                visited.add(ref.getOther(i));
                stack.push(ref.getOther(i));
                spanningTree.add(ref);
            }
        }

        return spanningTree;
    }

    public enum CircuitDirection {
        INVALID,
        UNDIRECTED,
        FORWARD, // ID1 TO ID2
        REVERSE; // ID2 TO ID1

        CircuitDirection relativeTo (int from, int to) {
            switch (this) {
                case FORWARD: return from > to ? CircuitDirection.REVERSE : CircuitDirection.FORWARD;
                case REVERSE: return from > to ? CircuitDirection.FORWARD : CircuitDirection.REVERSE;
                default: return CircuitDirection.UNDIRECTED;
            }
        }
    }
}
