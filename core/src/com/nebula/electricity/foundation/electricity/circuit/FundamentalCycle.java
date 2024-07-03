package com.nebula.electricity.foundation.electricity.circuit;

import com.nebula.electricity.foundation.electricity.component.ConnectionReference;

import java.util.*;
import java.util.stream.Collectors;

public class FundamentalCycle {
    List<ConnectionReference> connections;

    private FundamentalCycle (List<ConnectionReference> connections) {
        this.connections = connections;
    }

    public static FundamentalCycle generate (Collection<ConnectionReference> spanningTree, ConnectionReference removed) {
        List<ConnectionReference> all = new ArrayList<>(spanningTree);
        all.add(removed);

        List<Integer> nodes = new Pruner(all).prune(removed);

        if (nodes.size() == 0)
            return new FundamentalCycle(List.of());

        // TODO: orient the connections
        // For now, simply orient them going forwards.

        all.clear();
        for (int i = 0; i < nodes.size() - 1; i++) {
            all.add(ConnectionReference.of(nodes.get(i), nodes.get(i + 1)));
        }
        all.add(ConnectionReference.of(nodes.get(nodes.size() - 1), nodes.get(0)));

        return new FundamentalCycle(all);
    }

    public List<ConnectionReference> getConnections () { return connections; }

    private static class Pruner {
        List<Integer> path;
        List<ConnectionReference> visited;
        List<ConnectionReference> all;

        Pruner (List<ConnectionReference> all) {
            this.all = all;
            this.visited = new ArrayList<>();
            this.path = new ArrayList<>();
        }

        // Returns the list of nodes which are present in the fundamental ring
        List<Integer> prune (ConnectionReference removed) {
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
            List<ConnectionReference> adjacent = all.stream()
                    .filter(ref -> ref.containsVertex(at))
                    .filter(ref -> !visited.contains(ref))
                    .collect(Collectors.toList());

            if (adjacent.size() == 0) // We have reached the end of a branch.
                return false;

            for (ConnectionReference ref : adjacent) {
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
