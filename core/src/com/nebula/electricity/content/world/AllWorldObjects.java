package com.nebula.electricity.content.world;

import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.content.world.object.AxisObject;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;

public class AllWorldObjects {
    public static final WorldObjectCreator<SimpleObject> CUBE = WorldObjectCreator.create()
            .withTexture("cube")
            .oneByOne()
            .finished();
   public static final WorldObjectCreator<AxisObject> CYLINDER = WorldObjectCreator.create(AxisObject::new)
           .withTextures(obj -> {
               obj.horizontal = ElectricitySimulator.getObjectTexture("cylinder/horizontal");
               obj.vertical = ElectricitySimulator.getObjectTexture("cylinder/vertical");
           })
           .withProperties(p -> p.add("horizontal", false))
           .oneByOne()
           .finished();

    public static void register () {}
}
