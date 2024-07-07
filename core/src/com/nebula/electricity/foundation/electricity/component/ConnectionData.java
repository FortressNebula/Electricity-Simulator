package com.nebula.electricity.foundation.electricity.component;

public class ConnectionData {
    // Electrical information
    float resistance;
    float voltage; // FROM SMALLER NODE ID TO BIGGER NODE ID
    float current;

    public boolean isInternal;
    public boolean shouldDraw;

    public ConnectionData (float voltage, float current, float resistance, boolean shouldDraw, boolean isInternal) {
        this.voltage = voltage;
        this.resistance = resistance;
        this.current = current;
        this.shouldDraw = shouldDraw;
        this.isInternal = isInternal;
    }

    // if ID 1 is greater than ID 2, the order will be swapped in the connection class
    // account for this by reversing the voltage
    public static ConnectionData internal (float voltage, float resistance, boolean isID1BiggerThanID2) {
        return new ConnectionData(isID1BiggerThanID2 ? -voltage : voltage, 0, resistance, false, true);
    }

    public static ConnectionData external () {
        return new ConnectionData(0,0,0, true, false);
    }
}
