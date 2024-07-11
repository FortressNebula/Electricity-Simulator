package com.nebula.electricity.foundation.electricity.circuit;

import Jama.Matrix;
import com.badlogic.gdx.graphics.Color;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.Connection;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class Circuit {
    Color debugColour;

    List<FundamentalCycle> fundamentalCycles;
    List<Connection> connections;

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

    public static Circuit make (Connection first) {
        return new Circuit().include(first);
    }

    public boolean containsVertex (int id) {
        for (Connection ref : connections)
            if (ref.containsVertex(id))
                return true;

        return false;
    }

    public Circuit include (Connection ref) {
        connections.add(ref);
        update();
        return this;
    }

    public Circuit include (Collection<Connection> refs) {
        connections.addAll(refs);
        update();
        return this;
    }

    public Circuit exclude (Connection ref) {
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

    public void update () {
        fundamentalCycles.clear();

        // Reset all connections
        connections.forEach(ref -> ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(ref).reset());

        generateCycles();

        if (fundamentalCycles.isEmpty())
            return;

        solve();
    }

    void solve () {
        Matrix resistances = new Matrix(fundamentalCycles.size(), fundamentalCycles.size(), 0);
        Matrix voltages = new Matrix(fundamentalCycles.size(), 1, 0);
        
        // Populate matrices
        for (int i = 0; i < fundamentalCycles.size(); i++) {
            // Set voltage by summing up voltage rises in the cycle
            FundamentalCycle primaryCycle = fundamentalCycles.get(i);

            voltages.set(i, 0, primaryCycle.connections.stream()
                    .mapToDouble(connection -> ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(connection)
                            .getVoltage(connection.getID1() > connection.getID2() ? CircuitDirection.REVERSE : CircuitDirection.FORWARD))
                    .sum()
            );

            // Set resistance row by looping through fundamental cycles and finding common branches
            for (int j = 0; j < fundamentalCycles.size(); j++) {
                FundamentalCycle secondaryCycle = fundamentalCycles.get(j);

                // Sum up all the resistances of the common branches
//                double resistance = secondaryCycle.connections.stream()
//                        // Look for common connections
//                        .filter(connection -> primaryCycle.connections.contains(connection) || primaryCycle.connections.contains(connection.reverse()))
//                        .mapToDouble(connection ->
//                                ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(connection).getResistance() *
//                                        (primaryCycle.connections.contains(connection.reverse()) ? -1 : 1))
//                        .sum();

                // Non-functional approach for debugging
                List<Connection> common = secondaryCycle.connections.stream()
                        .filter(connection -> primaryCycle.connections.contains(connection) || primaryCycle.connections.contains(connection.reverse()))
                        .collect(Collectors.toList());

                double resistance = 0.0;

                for (Connection c : common) {
                    resistance += ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(c).getResistance() *
                                        (primaryCycle.connections.contains(c.reverse()) ? -1 : 1);
                }

                resistances.set(i, j, resistance);
            }
        }

        // Solve!
        Matrix currents = new Matrix(fundamentalCycles.size(), 1);

        //System.out.println("RESISTANCES");
        //resistances.print(3, 3);

        try {
            currents = resistances.solve(voltages);
            //System.out.println("CURRENTS");
            //currents.print(3, 3);
        } catch (RuntimeException e) {
            System.out.println("FAILED!!");
            e.printStackTrace();
        }

        for (int i = 0; i < fundamentalCycles.size(); i++) {
            fundamentalCycles.get(i).setCurrent(currents.get(i, 0));
            fundamentalCycles.get(i).addCurrentToConnections();
        }
    }

    void generateCycles () {
        List<Connection> spanningTree = getSpanningTree();
        if (spanningTree.isEmpty())
            return;

        List<Connection> removedConnections = connections.stream()
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
            return new FundamentalCycle(List.of());

        nodes.add(nodes.get(0)); // Just to make it easier to loop around

        for (int i = 0; i < nodes.size() - 1; i++) {
            int from = nodes.get(i);
            int to = nodes.get(i + 1);

            cycleConnections.add(Connection.directed(from, to));
        }

        return new FundamentalCycle(cycleConnections);
    }

    public List<FundamentalCycle> getCycles () { return fundamentalCycles; }
    public List<Connection> getConnections () { return connections; }

    public Color getDebugColour () { return debugColour; }
    public void setDebugColour (Color colour) { debugColour = colour; }

    // Solving mechanics

    public List<Connection> getSpanningTree () {
        if (connections.isEmpty())
            return List.of();

        List<Integer> visited = new ArrayList<>();
        List<Connection> spanningTree = new ArrayList<>();

        ArrayDeque<Integer> stack = new ArrayDeque<>();
        // It shouldn't be this hard to just get one item out of a set jesus christ
        stack.add(connections.get(0).getID1());

        // DFS woohoo
        while (!stack.isEmpty()) {
            int i = stack.pop();

            // Check all connections that feature this vertex and have NOT been visited
            List<Connection> adjacent = connections.stream()
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
        FORWARD, // ID1 TO ID2
        REVERSE  // ID2 TO ID1
    }
}
