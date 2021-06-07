package com.game.engine;

import com.game.engine.engine_ignition;
import com.game.engine.render;

public abstract class abstractGame {
	public abstract void update(engine_ignition ei, float delta_time);

	public abstract void render(engine_ignition ei, render r);
}
