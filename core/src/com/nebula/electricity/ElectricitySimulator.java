package com.nebula.electricity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nebula.electricity.content.Module;
import com.nebula.electricity.content.world.World;

import java.util.Arrays;

/**
 * Main class that handles all the modules of the simulator
 * Responsible for rendering modules with attachered renderers, and disposing them too
 */
public class ElectricitySimulator extends ApplicationAdapter {
	// All modules
	public static Module[] MODULES = new Module[]{};

	public static final World WORLD = add(new World());

	// Rendering information
	SpriteBatch batch;
	OrthographicCamera camera;

	@Override
	public void create () {
		// Initialise rendering tools
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Init all the modules
		WORLD.init(5, 5);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		for (Module m : MODULES)
			if (m.doesDraw()) m.draw(batch, camera);
	}

	@Override
	public void resize (int width, int height) {
		camera.setToOrtho(false, width, height);
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (Module m : MODULES)
			m.dispose();
	}

	static <T extends Module> T add(T module) {
		MODULES = Arrays.copyOf(MODULES, MODULES.length + 1);
		MODULES[MODULES.length - 1] = module;
		return module;
	}
}
