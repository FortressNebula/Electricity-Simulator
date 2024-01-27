package com.nebula.electricity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nebula.electricity.content.Module;
import com.nebula.electricity.content.input.InputManager;
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
	public static final InputManager INPUT_MANAGER = add(new InputManager());

	// Rendering information
	private static final Color BACKGROUND_COLOUR = Color.valueOf("5e6385");
	private static SpriteBatch batch;
	private static OrthographicCamera camera;

	// Application listener implementation methods
	@Override
	public void create () {
		// Initialise rendering tools
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		// Init all the modules
		WORLD.init(6, 6);
		INPUT_MANAGER.init();
	}

	@Override
	public void render () {
		for (Module m : MODULES)
			m.update();

		ScreenUtils.clear(BACKGROUND_COLOUR);
		for (Module m : MODULES)
			if (m.doesDraw()) m.draw(batch, camera);
	}

	@Override
	public void resize (int width, int height) {
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();
	}

	@Override
	public void dispose () {
		batch.dispose();
		for (Module m : MODULES)
			m.dispose();
	}

	// Module utilities
	private static <T extends Module> T add (T module) {
		MODULES = Arrays.copyOf(MODULES, MODULES.length + 1);
		MODULES[MODULES.length - 1] = module;
		return module;
	}

	public static void cameraTranslate (float dx, float dy) {
		System.out.println("Translated camera!");
		camera.translate(dx, dy);
		camera.update();
	}
}
