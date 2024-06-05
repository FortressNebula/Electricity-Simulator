package com.nebula.electricity.foundation.electricity;

import java.util.Objects;

public class ConnectionReference {
    final int id1;
    final int id2;

    private ConnectionReference (int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    public static ConnectionReference fromIDs (int i, int j) {
        return new ConnectionReference(i, j);
    }

    public boolean containsNode (int id) {
        return id1 == id || id2 == id;
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
        return Objects.hash(id1, id2);
    }
}
