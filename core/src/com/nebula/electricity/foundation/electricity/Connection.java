package com.nebula.electricity.foundation.electricity;

public class Connection {
    // Electrical information
    float resistance;
    float voltage;
    float current;

    public boolean shouldDraw;

    public Connection (float voltage, float current, float resistance, boolean shouldDraw) {
        this.voltage = voltage;
        this.resistance = resistance;
        this.current = current;
        this.shouldDraw = shouldDraw;
    }

    public Connection (float voltage, float resistance, boolean shouldDraw) {
        this(voltage, 0, resistance, shouldDraw);
    }

    public Connection (float voltage, float resistance) {
        this(voltage, 0, resistance, true);
    }
}
