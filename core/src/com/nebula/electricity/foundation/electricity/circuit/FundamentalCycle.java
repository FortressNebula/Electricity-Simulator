package com.nebula.electricity.foundation.electricity.circuit;

import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.Connection;

import java.util.*;
import java.util.stream.Collectors;

public class FundamentalCycle {
    List<Connection> connections;
    Circuit.CircuitDirection direction;
    double current;

    FundamentalCycle (List<Connection> connections, Circuit.CircuitDirection direction) {
        this.connections = connections;
        this.direction = direction;
        this.current = 0;
    }

    void setCurrent (double current) {
        this.current = current;
    }

    void addCurrentToConnections () {
        for (Connection connection : connections) {
            ElectricitySimulator.ELECTRICITY.CONNECTIONS.get(connection).addCurrent(current);
        }
    }

    public List<Connection> getConnections () { return connections; }

    static class Pruner {
        List<Integer> path;
        List<Connection> visited;
        List<Connection> all;

        Pruner (List<Connection> all) {
            this.all = all;
            this.visited = new ArrayList<>();
            this.path = new ArrayList<>();
        }

        // Returns the list of nodes which are present in the fundamental ring
        List<Integer> prune (Connection removed) {
            //Starting from one of the removed nodes as the root of a DFS tree, we can search until we find the other removed node.
            findPath(0, removed.getID1(), removed.getID2());
            return path;
        }

        private boolean findPath (int depth, int at, int destination) {
            if (at == destination) { // We found it??
                if (depth == 1) // If depth == 1, then we have simply gone down a back edge. This is not allowed.
                    return false;
                else {
                    path.add(at);
                    return true;
                }
            }

            // Check all connections that feature this vertex and have NOT been visited
            List<Connection> adjacent = all.stream()
                    .filter(ref -> ref.containsVertex(at))
                    .filter(ref -> !visited.contains(ref))
                    .collect(Collectors.toList());

            if (adjacent.size() == 0) // We have reached the end of a branch.
                return false;

            for (Connection ref : adjacent) {
                // Visit each node
                visited.add(ref);
                if (findPath(depth + 1, ref.getOther(at), destination)) {
                    // Found the destination down here!! Save this node to the path
                    path.add(at);
                    return true;
                }
            }

            return false;
        }
    }
}
