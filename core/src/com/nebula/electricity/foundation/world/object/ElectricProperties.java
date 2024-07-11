package com.nebula.electricity.foundation.world.object;

import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.electricity.component.ConnectionData;
import com.nebula.electricity.foundation.electricity.component.Node;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.*;
import java.util.stream.Collectors;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class ElectricProperties {
    // Terminals
    protected final List<Node> nodes;
    protected final Set<Integer> ids;
    protected Connection registeredConnectionID;
    // Electrical information
    protected float resistance;
    protected float voltage;
    protected float maxCurrent;
    // Update behaviour
    protected Runnable onInternalConnectionUpdate = () -> {};
    protected boolean shouldHandleInternalConnectionAutomatically;

    public ElectricProperties () {
        this.nodes = new ArrayList<>();
        this.ids = new HashSet<>();
        this.registeredConnectionID = null;
        this.voltage = 0;
        this.resistance = 0;
        this.maxCurrent = 100;
        this.shouldHandleInternalConnectionAutomatically = true;
    }

    public List<Node> getNodes () { return nodes; }
    public float getMaxCurrent () { return maxCurrent; }

    public boolean containsNode (int id) { return ids.contains(id); }

    public void update () {
        // Count number of connected nodes
        List<Node> connected = nodes.stream()
                .filter(Node::getConnected)
                .collect(Collectors.toList());

        if (connected.size() > 2)
            throw new IllegalStateException("Too many connections on a single object!");

        if (connected.size() < 2) {
            registeredConnectionID = null;
            onInternalConnectionUpdate.run();
            return;
        }

        registerUniqueInternalConnection(connected.get(0).getID(), connected.get(1).getID());
    }

    public void recheckInternalConnection () {
        if (nodes.stream().mapToInt(n -> n.getConnected() ? 1 : 0).sum() < 2 && registeredConnectionID != null) {
            // We have lost our internal connection
            nodes.forEach(n -> n.setEnabled(true));
            if (shouldHandleInternalConnectionAutomatically)
                ELECTRICITY.CONNECTIONS.delete(registeredConnectionID);
            registeredConnectionID = null;
            onInternalConnectionUpdate.run();
        }
    }

    // Registers an internal connection
    void registerInternalConnection (int id1, int id2) {
        if (shouldHandleInternalConnectionAutomatically)
            ELECTRICITY.CONNECTIONS.add(Connection.of(id1, id2), ConnectionData.internal(voltage, resistance, id1 > id2));
        registeredConnectionID = Connection.directed(id1, id2);
        onInternalConnectionUpdate.run();
    }

    // Registers an internal connection and disables all the other nodes
    void registerUniqueInternalConnection (int id1, int id2) {
        registerInternalConnection(id1, id2);

        nodes.forEach( n -> {
            if (n.getID() != id1 && n.getID() != id2)
                n.setEnabled(false);
        });
    }

    public Optional<Connection> getInternalID () {
        return Optional.ofNullable(registeredConnectionID);
    }

    // Builder methods
    public ElectricProperties resistance (float resistance) {
        this.resistance = resistance;
        return this;
    }

    public ElectricProperties voltage (float voltage) {
        this.voltage = voltage;
        return this;
    }

    public ElectricProperties maxCurrent (float current) {
        this.maxCurrent = current;
        return this;
    }

    public ElectricProperties addNode (int x, int y, Direction direction) {
        Node n = new Node(x, y, direction);
        nodes.add(n);
        ids.add(n.getID());
        return this;
    }

    public ElectricProperties buildAt (Vector2i position, Vector2i size, Direction direction) {
        nodes.forEach(n -> n.rotate(size, direction).moveTo(position));
        return this;
    }
}
