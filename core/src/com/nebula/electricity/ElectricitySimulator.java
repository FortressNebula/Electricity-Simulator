package com.nebula.electricity;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.ScreenUtils;
import com.nebula.electricity.content.Module;
import com.nebula.electricity.content.world.World;

import java.util.Arrays;

public class ElectricitySimulator extends ApplicationAdapter {
	// references to submodules
	public static Module[] MODULES = new Module[]{};
	public static final World WORLD = add(new World());

	@Override
	public void create () {
		// Init all the submodules
		WORLD.init(5, 5);
	}

	@Override
	public void render () {
		ScreenUtils.clear(0, 0, 0, 1);
		for (Module m : MODULES) {
			if (m.hasRenderer()) m.getRenderer().draw();
		}
	}

	@Override
	public void resize (int width, int height) {
		for (Module m : MODULES) {
			if (m.hasRenderer()) m.getRenderer().onResize(width, height);
		}
	}

	@Override
	public void dispose () {
		for (Module m : MODULES) {
			if (m.hasRenderer()) m.getRenderer().dispose();
			m.dispose();
		}
	}

	static <T extends Module> T add(T module) {
		MODULES = Arrays.copyOf(MODULES, MODULES.length + 1);
		MODULES[MODULES.length - 1] = module;
		return module;
	}
}
