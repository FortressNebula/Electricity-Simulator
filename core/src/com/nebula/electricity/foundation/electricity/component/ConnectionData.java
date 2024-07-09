package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.foundation.electricity.circuit.Circuit;

public class ConnectionData {
    // Electrical information
    double resistance;
    double voltage; // FROM SMALLER NODE ID TO BIGGER NODE ID
    double current;

    public boolean isInternal;
    public boolean shouldDraw;

    public ConnectionData (double voltage, double current, double resistance, boolean shouldDraw, boolean isInternal) {
        this.voltage = voltage;
        this.resistance = resistance;
        this.current = current;
        this.shouldDraw = shouldDraw;
        this.isInternal = isInternal;
    }

    // if ID 1 is greater than ID 2, the order will be swapped in the connection class
    // account for this by reversing the voltage
    public static ConnectionData internal (double voltage, double resistance, boolean isID1BiggerThanID2) {
        return new ConnectionData(isID1BiggerThanID2 ? -voltage : voltage, 0, resistance, false, true);
    }

    public static ConnectionData external () {
        return new ConnectionData(0,0,0, true, false);
    }

    public double getVoltage (Circuit.CircuitDirection inDirection) {
        return inDirection == Circuit.CircuitDirection.FORWARD ? voltage : -voltage;
    }

    public double getResistance () {
        return resistance;
    }

    // Modification

    public void reset () {
        current = 0;
    }
    public void addCurrent (double current) {
        this.current += current;
    }
}
