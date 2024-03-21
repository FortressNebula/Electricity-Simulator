package com.nebula.electricity.foundation;

import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public interface Module extends Disposable {
    void init ();

    default void draw (SpriteBatch batch, Camera camera) {}
    default void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {} // Shape Renderer is only available in GUI drawing
    default void update () {}
    default void dispose () {}
}
