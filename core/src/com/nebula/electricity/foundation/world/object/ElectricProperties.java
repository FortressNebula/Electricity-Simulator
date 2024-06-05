package com.nebula.electricity.foundation.world.object;

import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.electricity.Connection;
import com.nebula.electricity.foundation.electricity.ConnectionReference;
import com.nebula.electricity.foundation.electricity.Node;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ElectricProperties {
    // Terminals
    final List<Node> nodes;
    final Set<Integer> ids;
    ConnectionReference registeredConnectionID;
    // Electrical information
    float resistance;
    float voltage;
    // Update information
    boolean isDirty;

    protected ElectricProperties (List<Node> nodes, float voltage, float resistance) {
        this.nodes = nodes;
        this.ids = nodes.stream()
                .map(Node::getID)
                .collect(Collectors.toSet());
        this.registeredConnectionID = null;
        this.voltage = voltage;
        this.resistance = resistance;

        isDirty = false;
    }

    public List<Node> getNodes () { return nodes; }

    public boolean containsNode (int id) { return ids.contains(id); }

    public void markDirty () { isDirty = true; }

    public void update () {
        if (!isDirty)
            return;

        // Count number of connected nodes
        List<Node> connected = nodes.stream()
                .filter(Node::getConnected)
                .collect(Collectors.toList());

        if (connected.size() > 2)
            throw new IllegalStateException("Too many connections on a single object!");

        if (connected.size() < 2) {
            registeredConnectionID = null;
            return;
        }

        registerUniqueInternalConnection(connected.get(0).getID(), connected.get(1).getID());
    }

    public void recheckConnected () {
        nodes.forEach(n -> n.setConnected(false));

        for (ConnectionReference ref : ElectricitySimulator.CIRCUIT_MANAGER.getAllConnections().keySet()) {
            // Discard internal connections
            if (!ElectricitySimulator.CIRCUIT_MANAGER.getConnection(ref).shouldDraw)
                continue;
            // Ignore connections that don't concern us
            if (!containsNode(ref.getID1()) && !containsNode(ref.getID2()))
                continue;

            nodes.forEach(n -> {
                if (n.getID() == ref.getID1() || n.getID() == ref.getID2())
                    n.setConnected(true);
            });
        }

        if (nodes.stream().mapToInt(n -> n.getConnected() ? 1 : 0).sum() < 2 && registeredConnectionID != null) {
            // We have lost our internal connection
            nodes.forEach(n -> n.setEnabled(true));
            ElectricitySimulator.CIRCUIT_MANAGER.removeConnection(registeredConnectionID);
            registeredConnectionID = null;
        }
    }

    // Registers an internal connection
    public void registerInternalConnection (int id1, int id2) {
        ElectricitySimulator.CIRCUIT_MANAGER.registerConnection(id1, id2, new Connection(voltage, resistance, false));
        registeredConnectionID = ConnectionReference.fromIDs(id1, id2);
    }

    // Registers an internal connection and disables all the other nodes
    public void registerUniqueInternalConnection (int id1, int id2) {
        registerInternalConnection(id1, id2);

        nodes.forEach( n -> {
            if (n.getID() != id1 && n.getID() != id2)
                n.setEnabled(false);
        });
    }

    // Use in the WorldObject#makeElectricHandler function
    public static Builder make () { return new Builder(); }

    public static class Builder {
        List<Node> nodes;
        float resistance;
        float voltage;

        private Builder () {
            nodes = new ArrayList<>();
            voltage = 0f;
            resistance = 0f;
        }

        public Builder resistance (float resistance) {
            this.resistance = resistance;
            return this;
        }

        public Builder voltage (float voltage) {
            this.voltage = voltage;
            return this;
        }

        public Builder addNode (int x, int y, Direction direction) {
            nodes.add(new Node(x, y, direction));
            return this;
        }

        public ElectricProperties buildAt (Vector2i position, Vector2i size, Direction direction) {
            nodes.forEach(n -> n.rotate(size, direction).moveTo(position));
            return new ElectricProperties(nodes, voltage, resistance);
        }
    }
}
