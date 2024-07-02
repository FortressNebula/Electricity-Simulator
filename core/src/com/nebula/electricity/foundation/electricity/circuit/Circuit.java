package com.nebula.electricity.foundation.electricity.circuit;

import com.badlogic.gdx.graphics.Color;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.ConnectionReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Circuit {
    Color debugColour;
    List<FundamentalCycle> fundamentalCycles;
    List<ConnectionReference> connections;

    private Circuit () {
        debugColour = new Color(ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                1f);
        fundamentalCycles = new ArrayList<>();
        connections = new ArrayList<>();
    }

    public static Circuit make () {
        return new Circuit();
    }

    public static Circuit make (ConnectionReference first) {
        return new Circuit().include(first);
    }

    public boolean containsVertex (int id) {
        for (ConnectionReference ref : connections) {
            if (ref.containsVertex(id))
                return true;
        }
        return false;
    }

    public Circuit include (ConnectionReference ref) {
        connections.add(ref);
        update();
        return this;
    }

    public Circuit include (Collection<ConnectionReference> refs) {
        connections.addAll(refs);
        update();
        return this;
    }

    public Circuit exclude (ConnectionReference ref) {
        connections.remove(ref);
        update();
        return this;
    }

    public boolean isEmpty () {
        return connections.isEmpty();
    }

    public void merge (Circuit other) {
        include(other.connections);
    }

    // Using the newly-collected array of connections, update the fundamental cycles
    public void update () {
        List<ConnectionReference> spanningTree = getSpanningTree();
        if (spanningTree.isEmpty())
            return;

        fundamentalCycles.clear();
        List<ConnectionReference> removedConnections = connections.stream()
                .filter(ref -> !spanningTree.contains(ref))
                .collect(Collectors.toList());

        for (ConnectionReference ref : removedConnections)
            fundamentalCycles.add(FundamentalCycle.generate(spanningTree, ref));
    }

    public List<FundamentalCycle> getCycles () { return fundamentalCycles; }
    public List<ConnectionReference> getConnections () { return connections; }

    public Color getDebugColour () { return debugColour; }
    public void setDebugColour (Color colour) { debugColour = colour; }

    // Solving mechanics

    public List<ConnectionReference> getSpanningTree () {
        if (connections.isEmpty())
            return List.of();

        List<Integer> visited = new ArrayList<>();
        List<ConnectionReference> spanningTree = new ArrayList<>();

        visited.add(connections.get(0).getID1());

        for (ConnectionReference ref : connections) {
            boolean containsID1 = visited.contains(ref.getID1());
            boolean containsID2 = visited.contains(ref.getID2());

            if (containsID1 && !containsID2) {
                spanningTree.add(ref);
                visited.add(ref.getID2());
            }

            if (containsID2 && !containsID1) {
                spanningTree.add(ref);
                visited.add(ref.getID1());
            }
        }

        return spanningTree;
    }
}
