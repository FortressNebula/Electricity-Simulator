package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.foundation.events.Events;
import com.nebula.electricity.math.Vector2i;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class WorldObjectCreator<T extends WorldObject> {
    Function<Vector2i, T> builder;
    TextureAtlas.AtlasRegion texture;

    private WorldObjectCreator (Function<Vector2i, T> builder, String textureName) {
        this.builder = builder
                .andThen(object -> {
                    object.texture = texture;
                    return object;
                });
        Events.INIT.add(() -> texture = ElectricitySimulator.getObjectTexture(textureName));
    }

    // Create an object using the factory
    public T create (Vector2i at) {
        return builder.apply(at);
    }

    // Factory
    public static Factory<WorldObject> create () {
        return new Factory<>(WorldObject::new);
    }
    public static <T extends WorldObject> Factory<T> create (Supplier<T> builder) {
        return new Factory<>(builder);
    }

    public static class Factory<T extends WorldObject> {
        Supplier<T> builder;

        String textureName;
        boolean isTicking;
        Vector2i size;
        Consumer<WorldObjectProperties> propertiesBuilder;
        Consumer<T> onObjectCreated;

        private Factory (Supplier<T> builder) {
            this.builder = builder;
            textureName = "";
            isTicking = false;
            propertiesBuilder = $ -> {};
            onObjectCreated = $ -> {};
            size = Vector2i.INVALID;
        }

        // Attach a texture to the object
        public Factory<T> withTexture (String name) {
            textureName = name;
            return this;
        }

        // Assign a size to the object
        public Factory<T> withSize (int x, int y) {
            size = new Vector2i(x, y);
            return this;
        }

        public Factory<T> oneByOne () {
            size = Vector2i.ONE_BY_ONE;
            return this;
        }

        // Set whether the object ticks or not
        public Factory<T> isTicking () {
            isTicking = true;
            return this;
        }

        // Assign properties to the object
        public Factory<T> withProperties (Consumer<WorldObjectProperties> cons) {
            propertiesBuilder = cons;
            return this;
        }

        // Do any other fiddling with the object when its created
        // Can include posting any object-creation events
        // /!\ IMPORTANT - OBJECT'S TEXTURE IS NOT AVAILABLE DURING THIS TIME.
        public Factory<T> onCreation (Consumer<T> cons) {
            onObjectCreated = cons;
            return this;
        }

        // Any other tasks to be executed, such as signing up to events
        // This finishes the factory
        public WorldObjectCreator<T> andDo (Runnable r) {
            r.run();
            return finished();
        }

        // Finish the factory
        public WorldObjectCreator<T> finished () {
            validate();
            return new WorldObjectCreator<>(at -> {
                T object = builder.get();
                object.isTicking = isTicking;
                object.position = at;
                object.size = size;
                object.max = at.add(size).add(-1);
                propertiesBuilder.accept(object.properties);
                onObjectCreated.accept(object);
                return object;
            }, textureName);
        }

        // Ensure important fields have been filled out
        private void validate () {
            if (textureName.isEmpty()) throw new NullPointerException("Object factory does not specify texture!");
            if (!size.isValid()) throw new IndexOutOfBoundsException("Object factory does not specify size!");
        }
    }
}
