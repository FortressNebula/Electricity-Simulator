package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.foundation.Module;

import java.util.HashMap;

public class CircuitManager implements Module {
    private final HashMap<ConnectionReference, Connection> allConnections;

    public CircuitManager () {
        this.allConnections = new HashMap<>();
    }

    @Override
    public void init () {

    }

    public void registerConnection (int id1, int id2, Connection connection) {
        allConnections.put(ConnectionReference.fromIDs(id1, id2), connection);
    }

    public void removeConnection (ConnectionReference ref) {
        allConnections.remove(ref);
    }

    public void removeConnection (int id1, int id2) {
        allConnections.remove(ConnectionReference.fromIDs(id1, id2));
    }

    public Connection getConnection (ConnectionReference ref) {
        return allConnections.get(ref);
    }

    public HashMap<ConnectionReference, Connection> getAllConnections () {
        return allConnections;
    }

    public void clearConnections () {
        allConnections.clear();
    }
}
