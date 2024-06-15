package com.nebula.electricity.foundation.electricity.circuit;

import com.badlogic.gdx.graphics.Color;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.component.ConnectionReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class Circuit {
    Color debugColour;
    List<ConnectionReference> connections;

    private Circuit () {
        debugColour = new Color(ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                ElectricitySimulator.RANDOM.nextFloat(),
                1f);
        connections = new ArrayList<>();
    }

    public static Circuit make () {
        return new Circuit();
    }

    public static Circuit make (ConnectionReference first) {
        Circuit out = new Circuit();
        out.include(first);
        return out;
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
        return this;
    }

    public Circuit include (Collection<ConnectionReference> refs) {
        connections.addAll(refs);
        return this;
    }

    public Circuit exclude (ConnectionReference ref) {
        connections.remove(ref);
        return this;
    }

    public boolean isEmpty () {
        return connections.isEmpty();
    }

    public void merge (Circuit other) {
        connections.addAll(other.connections);
    }

    public List<ConnectionReference> getConnections () { return connections; }

    public Color getDebugColour () { return debugColour; }
    public void setDebugColour (Color colour) { debugColour = colour; }
}
