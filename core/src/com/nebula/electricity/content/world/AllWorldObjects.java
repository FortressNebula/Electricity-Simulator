package com.nebula.electricity.content.world;

import com.nebula.electricity.foundation.world.object.WorldObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;

public class AllWorldObjects {
    public static final WorldObjectCreator<WorldObject> CUBE = WorldObjectCreator.create()
            .withTexture("cube")
            .oneByOne()
            .finished();
   public static final WorldObjectCreator<WorldObject> CYLINDER = WorldObjectCreator.create()
           .withTexture("cylinder")
           .oneByOne()
           .finished();

    public static void register () {}
}
