package com.nebula.electricity.foundation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public interface Module extends Disposable {
    void init ();

    boolean doesDraw ();
    default void draw (SpriteBatch batch, Camera camera) {
        throw new NotImplementedException();
    }

    void update ();

    default void dispose () {}
}
