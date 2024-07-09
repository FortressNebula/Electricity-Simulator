package com.nebula.electricity.content.world;

import com.nebula.electricity.content.world.object.*;
import com.nebula.electricity.foundation.world.object.SimpleObject;
import com.nebula.electricity.foundation.world.object.WorldObjectCreator;

@SuppressWarnings("unused")
public class AllWorldObjects {

    public static final WorldObjectCreator<SimpleObject> CUBE = WorldObjectCreator.create("cube")
            .withTexture("cube")
            .oneByOne()
            .finished();
    // TODO: add tnt
//    public static final WorldObjectCreator<SimpleObject> TNT = WorldObjectCreator.create("tnt")
//            .withTexture("tnt")
//            .oneByOne()
//            .finished();
   public static final WorldObjectCreator<AxisObject> CYLINDER = WorldObjectCreator.create("cylinder", AxisObject::new)
           .withTextures(AxisObject.textures("cylinder"))
           .oneByOne()
           .finished();
   public static final WorldObjectCreator<BatteryObject> BATTERY = WorldObjectCreator.create("battery", BatteryObject::new)
           .withTextures(DirectionalObject.textures("battery"))
           .withSize(2, 1)
           .finished();
   public static final WorldObjectCreator<BulbObject> BULB = WorldObjectCreator.create("bulb", BulbObject::new)
           .withTextures(BulbObject.textures("bulb"))
           .oneByOne()
           .finished();
   public static final WorldObjectCreator<SwitchObject> SWITCH = WorldObjectCreator.create("switch", SwitchObject::new)
           .withTextures(SwitchObject.textures("switch"))
           .oneByOne()
           .finished();

    public static void register () {}
}
