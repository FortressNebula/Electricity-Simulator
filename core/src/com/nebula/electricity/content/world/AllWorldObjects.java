package com.nebula.electricity.content.world;

import com.nebula.electricity.content.world.object.AxisObject;
import com.nebula.electricity.content.world.object.DirectionalObject;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;

@SuppressWarnings("unused")
public class AllWorldObjects {

    public static final WorldObjectCreator<SimpleObject> CUBE = WorldObjectCreator.create("cube")
            .withTexture("cube")
            .oneByOne()
            .finished();
   public static final WorldObjectCreator<AxisObject> CYLINDER = WorldObjectCreator.create("cylinder", AxisObject::new)
           .withTextures(AxisObject.textures("cylinder"))
           .oneByOne()
           .finished();
   public static final WorldObjectCreator<DirectionalObject> BATTERY = WorldObjectCreator.create("battery", DirectionalObject::new)
           .withTextures(DirectionalObject.textures("battery"))
           .withSize(2, 1)
           .finished();

    public static void register () {}
}
