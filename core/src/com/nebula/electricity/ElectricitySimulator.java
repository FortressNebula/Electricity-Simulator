package com.nebula.electricity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nebula.electricity.content.world.AllWorldObjects;
import com.nebula.electricity.foundation.Module;
import com.nebula.electricity.foundation.events.Events;
import com.nebula.electricity.foundation.input.InputManager;
import com.nebula.electricity.foundation.world.World;
import com.nebula.electricity.math.Vector2i;

import java.util.Arrays;
import java.util.Random;

/**
 * Main class that handles all the modules of the simulator
 * Responsible for rendering modules with attachered renderers, and disposing them too
 */
@SuppressWarnings({"GDXJavaStaticResource", "unused"})
public class ElectricitySimulator extends ApplicationAdapter {
	// All modules
	public static Module[] MODULES = new Module[]{};

	public static final World WORLD = add(new World());
	public static final InputManager INPUT_MANAGER = add(new InputManager());

	// Rendering information
	private static final Color BACKGROUND_COLOUR = Color.valueOf("5e6385");
	private static TextureAtlas atlas;
	private static SpriteBatch batch;
	private static OrthographicCamera camera;
	private static OrthographicCamera guiCamera; // READ-ONLY
	private static boolean isCameraDirty;

	// General utilities
	public static final Random RANDOM = new Random();

	/**
	 * Responsible for initialising all the modules, as well as initialising the core rendering components
	 */
	@Override
	public void create () {
		// Initialise rendering tools
		batch = new SpriteBatch();
		camera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera = new OrthographicCamera(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		guiCamera.translate(Gdx.graphics.getWidth() * 0.5f, Gdx.graphics.getHeight() * 0.5f);
		atlas = new TextureAtlas("atlases/main.atlas");
		// Init all the modules
		for (Module m : MODULES)
			m.init();
		// Init registries
		AllWorldObjects.register();
		// Post initialisation event for any other objects/entities
		Events.INIT.post();
	}

	/**
	 * Responsible for updating and rendering all modules.
	 * If the camera needs to be updated, the matrices are calculated after all other modules are done updating
	 */
	@Override
	public void render () {
		// Update all modules
		for (Module m : MODULES)
			m.update();

		if (isCameraDirty) {
			camera.zoom = MathUtils.clamp(camera.zoom, 0.1F, 2F);
			camera.update();
		}

		// Draw all modules
		ScreenUtils.clear(BACKGROUND_COLOUR);
		batch.begin();
		batch.setProjectionMatrix(camera.combined);
		for (Module m : MODULES)
			m.draw(batch);
		batch.end();

		// Draw GUI
		batch.begin();
		batch.setProjectionMatrix(guiCamera.combined);
		batch.setColor(1,1,1,1);
		for (Module m : MODULES)
			m.drawGUI(batch);
		batch.end();
	}

	/**
	 * If the screen resizes, the camera also needs to be updated accordingly to fit the viewport size
	 * @param width the new width in pixels
	 * @param height the new height in pixels
	 */
	@Override
	public void resize (int width, int height) {
		// Update main camera used by world
		camera.viewportWidth = width;
		camera.viewportHeight = height;
		camera.update();

		// Update GUI camera used by input manager
		guiCamera.setToOrtho(false, width, height);
	}

	/**
	 * At the end of the application life cycle, all modules and rendering components are disposed of
	 */
	@Override
	public void dispose () {
		atlas.dispose();
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

	// Camera utilities

	/**
	 * Retrieve the camera used by the simulator, for use by other components to modify/use the camera
	 * for projection or movement.
	 * @param willBeDirty Whether the camera is being retrieved with the intention of modifying it
	 * @return The Orthographic camera used by the simulation
	 */
	public static OrthographicCamera getCamera (boolean willBeDirty) {
		isCameraDirty |= willBeDirty;
		return camera;
	}

	public static OrthographicCamera getCamera () {
		return getCamera(true);
	}

	public static Vector2i unproject (Vector2i screenPos) {
		return new Vector2i(camera.unproject(new Vector3(screenPos.x, screenPos.y, 0)));
	}

	// Atlas utilities

	public static TextureAtlas.AtlasRegion getTexture (String name) {
		return atlas.findRegion(name);
	}
	public static TextureAtlas.AtlasRegion getObjectTexture (String name) {
		return atlas.findRegion("objects/" + name);
	}
	public static TextureAtlas.AtlasRegion getGUITexture (String name) {
		return atlas.findRegion("gui/" + name);
	}

}
