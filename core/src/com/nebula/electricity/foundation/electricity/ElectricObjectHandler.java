package com.nebula.electricity.foundation.electricity;

import com.nebula.electricity.math.Direction;
import com.nebula.electricity.math.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ElectricObjectHandler {
    final List<Connection> connections;
    float resistance;

    protected ElectricObjectHandler (List<Connection> connections, float resistance) {
        this.connections = connections;
        this.resistance = resistance;
    }

    public List<Connection> getConnections () { return connections; }

    // Use in the WorldObject#makeElectricHandler function
    public static ElectricHandlerBuilder make () { return new ElectricHandlerBuilder(); }

    public static class ElectricHandlerBuilder {
        List<Connection> connections;
        float resistance;

        private ElectricHandlerBuilder () {
            connections = new ArrayList<>();
            resistance = 0f;
        }

        public ElectricHandlerBuilder withResistance (float resistance) {
            this.resistance = resistance;
            return this;
        }

        public ElectricHandlerBuilder addConnection (int x, int y, Direction direction) {
            connections.add(new Connection(new Vector2i(x, y), direction));
            return this;
        }

        public ElectricObjectHandler buildAt (Vector2i position, Vector2i size, Direction direction) {
            return new ElectricObjectHandler(
                    connections.stream()
                            .map(connection -> connection.rotate(size, direction).moveTo(position))
                            .collect(Collectors.toList()),
                    resistance
            );
        }
    }
}
