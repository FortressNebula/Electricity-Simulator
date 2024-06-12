package com.nebula.electricity.foundation.electricity.component;

import com.nebula.electricity.math.Vector2i;

public interface CircuitVertex {
    int getID ();
    Vector2i getRenderPosition ();
    boolean canConnect ();
}
