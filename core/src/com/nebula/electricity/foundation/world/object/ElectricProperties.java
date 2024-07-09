package com.nebula.electricity.foundation.world.object;

import com.nebula.electricity.foundation.electricity.component.ConnectionData;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.electricity.component.Node;
import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static com.nebula.electricity.ElectricitySimulator.ELECTRICITY;

public class ElectricProperties {
    // Terminals
    final List<Node> nodes;
    final Set<Integer> ids;
    Connection registeredConnectionID;
    // Electrical information
    float resistance;
    float voltage;

    Runnable onConnectionDestroyed = () -> {};

    protected ElectricProperties (List<Node> nodes, float voltage, float resistance) {
        this.nodes = nodes;
        this.ids = nodes.stream()
                .map(Node::getID)
                .collect(Collectors.toSet());
        this.registeredConnectionID = null;
        this.voltage = voltage;
        this.resistance = resistance;
    }

    public List<Node> getNodes () { return nodes; }

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
            onConnectionDestroyed.run();
            return;
        }

        registerUniqueInternalConnection(connected.get(0).getID(), connected.get(1).getID());
    }

    public void recheckInternalConnection () {
        if (nodes.stream().mapToInt(n -> n.getConnected() ? 1 : 0).sum() < 2 && registeredConnectionID != null) {
            // We have lost our internal connection
            nodes.forEach(n -> n.setEnabled(true));
            ELECTRICITY.CONNECTIONS.delete(registeredConnectionID);
            registeredConnectionID = null;
        }
    }

    // Registers an internal connection
    void registerInternalConnection (int id1, int id2) {
        ELECTRICITY.CONNECTIONS.add(Connection.of(id1, id2), ConnectionData.internal(voltage, resistance, id1 > id2));
        registeredConnectionID = Connection.of(id1, id2);
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
