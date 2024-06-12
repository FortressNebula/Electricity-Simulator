package com.nebula.electricity.foundation;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.utils.Disposable;

public interface Module extends Disposable {
    void init ();

    default void draw (SpriteBatch batch, ShapeRenderer shapes) {}
    default void drawGUI (SpriteBatch batch, ShapeRenderer shapes) {}
    default void update () {}
    default void dispose () {}
}
