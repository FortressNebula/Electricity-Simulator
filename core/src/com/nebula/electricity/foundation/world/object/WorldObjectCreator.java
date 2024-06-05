package com.nebula.electricity.foundation.world.object;

import com.badlogic.gdx.utils.Array;
import com.nebula.electricity.ElectricitySimulator;
import com.nebula.electricity.math.Vector2i;

import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Class for creating objects of a certain type. Shouldn't require inheriting to create custom objects as class
 * is already made to be super customizable
 * @param <T> The kind of world object the creator makes
 */
@SuppressWarnings("unused")
public class WorldObjectCreator<T extends WorldObject> {
    private static final Array<WorldObjectCreator<?>> ALL = new Array<>();

    final Function<Vector2i, T> builder;
    final String type;

    private WorldObjectCreator (String type, Function<Vector2i, T> builder) {
        this.type = type;
        this.builder = builder;
        ALL.add(this);
    }

    // Create an object using the factory
    public T create (Vector2i at) {
        return builder.apply(at);
    }

    // Get object type

    // Factory
    public static Builder<SimpleObject> create (String name) {
        return new Builder<>(name, SimpleObject::new);
    }
    public static <T extends WorldObject> Builder<T> create (String name, Supplier<T> builder) {
        return new Builder<>(name, builder);
    }

    // Interacting with all registered world object creators
    public static WorldObjectCreator<?> getCreatorFromIndex (int i) {
        return ALL.get(i);
    }
    public static int getNumCreators () {
        return ALL.size;
    }
    public static int getIndexFomObject (WorldObject object) {
        for (int i = 0; i < ALL.size; i++) {
            if (Objects.equals(ALL.get(i).type, object.type))
                return i;
        }
        throw new IllegalStateException("Unidentified (not-Flying) Object detected");
    }

    public static class Builder<T extends WorldObject> {
        String type;
        Supplier<T> builder;

        Vector2i size;
        Function<WorldObjectProperties, WorldObjectProperties> propertiesBuilder;
        Consumer<? super T> onObjectCreated;
        Consumer<? super T> loadTextures;
        boolean areTexturesDefined;

        private Builder (String type, Supplier<T> builder) {
            this.type = type;
            this.builder = builder;

            propertiesBuilder = $ -> $;
            onObjectCreated = $ -> {};
            loadTextures = $ -> {};

            areTexturesDefined = false;
            size = Vector2i.INVALID;
        }

        // Attach a texture to the object
        public Builder<T> withTexture (String name) {
            return withTextures(object -> ((SimpleObject) object).texture = ElectricitySimulator.getObjectTexture(name));
        }

        public Builder<T> withTextures (Consumer<? super T> textureLoader) {
            loadTextures = textureLoader;
            areTexturesDefined = true;
            return this;
        }

        // Assign a size to the object
        public Builder<T> withSize (int x, int y) {
            size = new Vector2i(x, y);
            return this;
        }

        public Builder<T> oneByOne () {
            size = Vector2i.ONE_BY_ONE;
            return this;
        }

        // Assign properties to the object
        public Builder<T> withProperties (Function<WorldObjectProperties, WorldObjectProperties> func) {
            propertiesBuilder = func;
            return this;
        }

        // Do any other fiddling with the object when its created
        // Can include posting any object-creation events
        // /!\ IMPORTANT - OBJECT'S TEXTURE IS NOT AVAILABLE DURING THIS TIME.
        public Builder<T> onCreation (Consumer<? super T> cons) {
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
            return new WorldObjectCreator<>(type, at -> {
                T object = builder.get();

                object.type = type;
                object.position = at;
                object.size = size;
                object.properties = propertiesBuilder.apply(object.initProperties());
                onObjectCreated.accept(object);
                loadTextures.accept(object);

                return object;
            });
        }

        // Ensure important fields have been filled out
        private void validate () {
            if (!areTexturesDefined) throw new NullPointerException("Object factory does not specify texture!");
            if (!size.isValid()) throw new IndexOutOfBoundsException("Object factory does not specify size!");
        }
    }
}
