package com.nebula.electricity.foundation.electricity.component;

import java.util.Objects;

public class Connection {
    final int id1;
    final int id2;

    private Connection (int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    // Always puts the smallest number first
    public static Connection of (int i, int j) {
        if (i < j)
            return new Connection(i, j);
        else
            return new Connection(j, i);
    }

    public static Connection of (CircuitVertex i, CircuitVertex j) {
        return of(i.getID(), j.getID());
    }

    public static Connection of (Connection directed) {
        return of(directed.id1, directed.id2);
    }

    public static Connection directed (int i, int j) {
        return new Connection(i, j);
    }

    public Connection undirect () {
        return Connection.of(this);
    }

    public Connection reverse () {
        return new Connection(id2, id1);
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
        Connection that = (Connection) o;

        return id1 == that.id1 && id2 == that.id2;
    }

    @Override
    public int hashCode () {
        return Objects.hash(id1, id2);
    }
}
