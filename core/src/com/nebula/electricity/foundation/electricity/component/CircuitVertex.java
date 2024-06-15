package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.math.Vector2i;

public interface CircuitVertex {
    int getID ();
    Vector2i getRenderPosition ();
    Vector2i getCombinedPosition ();
    boolean canConnect ();
    boolean getConnected ();
    void setConnected (boolean value);
}
