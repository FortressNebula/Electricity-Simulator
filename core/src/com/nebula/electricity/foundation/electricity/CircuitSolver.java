package com.nebula.electricity.foundation.electricity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CircuitSolver {
    static Set<ConnectionReference> constructSpanningTree (Set<ConnectionReference> graph) {
        List<Integer> traversedNodes = new ArrayList<>();
        Set<ConnectionReference> spanningTree = new HashSet<>();

        for (ConnectionReference ref : graph) {
            if (traversedNodes.size() == 0)
                traversedNodes.add(ref.id1);

            boolean hasID1 = traversedNodes.contains(ref.id1);
            boolean hasID2 = traversedNodes.contains(ref.id2);

            if (hasID1 == hasID2)
                continue;

            spanningTree.add(ref);
            if (hasID1)
                traversedNodes.add(ref.id2);
            if (hasID2)
                traversedNodes.add(ref.id1);
        }

        return spanningTree;
    }

}
