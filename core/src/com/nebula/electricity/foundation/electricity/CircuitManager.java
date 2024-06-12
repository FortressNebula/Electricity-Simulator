package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.electricity.component.CircuitVertex;
import com.nebula.electricity.foundation.electricity.component.Connection;
import com.nebula.electricity.foundation.electricity.component.ConnectionReference;

public class CircuitManager implements Module {
    public static int GLOBAL_VERTEX_ID = 0;

    public final ComponentManager<Integer, CircuitVertex> VERTICES;
    public final ComponentManager<ConnectionReference, Connection> CONNECTIONS;

    public CircuitManager () {
        VERTICES = new ComponentManager<>();
        CONNECTIONS = new ComponentManager<>();

        VERTICES.addIDFunction(CircuitVertex::getID);
    }

    @Override
    public void init () {

    }

    public void refreshConnectedNodes () {
        ElectricitySimulator.WORLD.forEachElectricalObject(object -> object.getElectricProperties().recheckConnected());
    }
}
