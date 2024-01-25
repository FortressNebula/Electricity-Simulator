package com.nebula.electricity.content;

import com.badlogic.gdx.utils.Disposable;

public interface Module {
    ModuleRenderer getRenderer ();
    boolean hasRenderer ();

    void update ();
    default void dispose () {}

    interface ModuleRenderer extends Disposable {
        void draw ();
        void onResize (int width, int height);
    }
}
