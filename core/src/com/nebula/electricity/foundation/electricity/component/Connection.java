package com.nebula.electricity.foundation.electricity.component;

public class Connection {
    // Electrical information
    float resistance;
    float voltage;
    float current;

    public boolean isInternal;
    public boolean shouldDraw;

    public Connection (float voltage, float current, float resistance, boolean shouldDraw, boolean isInternal) {
        this.voltage = voltage;
        this.resistance = resistance;
        this.current = current;
        this.shouldDraw = shouldDraw;
        this.isInternal = isInternal;
    }

    public static Connection internal (float voltage, float resistance) {
        return new Connection(voltage, 0, resistance, false, true);
    }

    public static Connection external () {
        return new Connection(0,0,0, true, false);
    }
}
