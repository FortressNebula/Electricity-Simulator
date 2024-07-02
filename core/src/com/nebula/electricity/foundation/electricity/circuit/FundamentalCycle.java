package com.nebula.electricity.foundation.electricity.circuit;

import com.nebula.electricity.foundation.electricity.component.ConnectionReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class FundamentalCycle {
    List<ConnectionReference> all;

    private FundamentalCycle (List<ConnectionReference> all) {
        this.all = all;
    }

    public static FundamentalCycle generate (Collection<ConnectionReference> spanningTree, ConnectionReference removed) {
        List<ConnectionReference> raw = new ArrayList<>(spanningTree);
        raw.add(removed);

        //TODO: Prune

        return new FundamentalCycle(raw);
    }

    public List<ConnectionReference> getAll () { return all; }
}
