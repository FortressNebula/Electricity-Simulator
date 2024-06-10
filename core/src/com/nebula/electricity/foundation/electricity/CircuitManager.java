package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;

import java.util.*;
import java.util.stream.Collectors;

public class CircuitManager implements Module {
    static int GLOBAL_VERTEX_ID = 0;

    private final HashMap<ConnectionReference, Connection> allConnections;
    private final HashMap<Integer, CircuitVertex> allVertices;

    private final List<ConnectionReference> connectionsToDelete;
    private final List<Integer> verticesToDelete;

    public CircuitManager () {
        allConnections = new HashMap<>();
        allVertices = new HashMap<>();
        connectionsToDelete = new ArrayList<>();
        verticesToDelete = new ArrayList<>();
    }

    @Override
    public void init () {

    }

    //region VERTICES

    public void refreshConnectedNodes () {
        ElectricitySimulator.WORLD.forEachElectricalObject(object -> object.getElectricProperties().recheckConnected());
    }

    public void registerVerticies (List<? extends CircuitVertex> vertices) {
        for (CircuitVertex v : vertices)
            allVertices.put(v.getID(), v);
    }

    public void deleteVertices (List<? extends CircuitVertex> vertices) {
        for (CircuitVertex v : vertices)
            allVertices.remove(v.getID());
    }

    public void registerVertex (CircuitVertex v) {
        allVertices.put(v.getID(), v);
    }

    public void deleteVertex (CircuitVertex v) {
        allVertices.remove(v.getID());
    }

    public void queueDeleteVertex (CircuitVertex v) {
        verticesToDelete.add(v.getID());
    }

    public Collection<CircuitVertex> getVertices () {
        return allVertices.values();
    }

    public CircuitVertex getVertex (int id) {
        return allVertices.get(id);
    }

    public List<Junction> getJunctions () {
        return allVertices.values().stream()
                .filter(v -> v instanceof Junction)
                .map(v -> (Junction) v)
                .collect(Collectors.toList());
    }

    public Junction getJunction (int id) {
        return (Junction) allVertices.get(id);
    }

    public List<Node> getNodes () {
        return allVertices.values().stream()
                .filter(v -> v instanceof Node)
                .map(v -> (Node) v)
                .collect(Collectors.toList());
    }

    public Node getNode (int id) { return (Node) allVertices.get(id); }

    //endregion

    //region CONNECTIONS

    public void registerConnection (int id1, int id2, Connection connection) {
        allConnections.put(ConnectionReference.fromIDs(id1, id2), connection);
        //test();
    }

    public void deleteConnection (ConnectionReference ref) {
        allConnections.remove(ref);
    }

    public void deleteConnection (int id1, int id2) {
        allConnections.remove(ConnectionReference.fromIDs(id1, id2));
    }

    // Useful when wishing to delete connections while iterating
    // Queues the deletions until ready
    public void queueDeleteConnection (ConnectionReference ref) { connectionsToDelete.add(ref); }

    public Connection getConnection (ConnectionReference ref) {
        return allConnections.get(ref);
    }

    public Set<ConnectionReference> getAllConnections () {
        return allConnections.keySet();
    }

    public void clearConnections () {
        allConnections.clear();
    }

    //endregion

    public void flushDeletionQueue () {
        for (ConnectionReference ref : connectionsToDelete)
            allConnections.remove(ref);
        connectionsToDelete.clear();

        for (Integer id : verticesToDelete)
            allVertices.remove(id);
        verticesToDelete.clear();
    }

}
