package com.nebula.electricity.foundation.electricity.component;

import java.util.Objects;

public class ConnectionReference {
    final int id1;
    final int id2;

    private ConnectionReference (int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    public static ConnectionReference of (int i, int j) {
        return new ConnectionReference(i, j);
    }

    public static ConnectionReference of (CircuitVertex i, CircuitVertex j) {
        return new ConnectionReference(i.getID(), j.getID());
    }

    public boolean containsVertex (int id) {
        return id1 == id || id2 == id;
    }

    public int getOther (int id) {
        if (id1 == id)
            return id2;
        if (id2 == id)
            return id1;
        return -1;
    }

    public int getID1 () { return id1; }
    public int getID2 () { return id2; }

    @Override
    public boolean equals (Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ConnectionReference that = (ConnectionReference) o;

        return (id1 == that.id1 && id2 == that.id2) || (id1 == that.id2 && id2 == that.id1);
    }

    @Override
    public int hashCode () {
        // Ensure the same connection reference has the same hash code
        if (id1 < id2)
            return Objects.hash(id1, id2);
        else
            return Objects.hash(id2, id1);
    }
}
