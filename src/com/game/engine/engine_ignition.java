package com.game.engine;

import java.awt.event.KeyEvent;

public class engine_ignition implements Runnable {
	// setting up instances
	private Thread thread; // threads allow to compute multiple things at once
	private window window;// window object
	private render renderer; // render object
	private input input;
	private abstractGame game;

	private boolean running = false;
	private final double UPDATE_CAP = 1.0 / 60.0;

	// default values, can be changed by the implementer
	private int width = 160, height = 120;
	private float scale = 4f;
	private String title = "FirstEngine v1.0";

	public engine_ignition(abstractGame game) { // constructor
		this.game = game;
	}

	public void start() {// acts like the "main" method of the engine
		window = new window(this);// window object takes in all the instance variables of engine_ignition
		thread = new Thread(this);// thread also takes in all instance variables of engine_ignition
		renderer = new render(this);
		input = new input(this);
		thread.run();

	}

	public void stop() {

	}

	public void run() {

		running = true;
		boolean render = true;

		double firsttime = 0;
		double lasttime = System.nanoTime() / 1000000000.0;
		double passedtime = 0;
		double unprocessedtime = 0;

		double frametime = 0;
		int frames = 0;
		int fps = 0;

		while (running) {// this while loop computer Each frame
			render = true;

			firsttime = System.nanoTime() / 1000000000.0; // start time in ms
			passedtime = firsttime - lasttime; // how long its been from start of the fucntion till now
			lasttime = firsttime; // swapped so loop keeps generating time differences accurately
			unprocessedtime += passedtime; // unprocessed time so we can decide if we wanna run the frames
			frametime += passedtime;

			// this is the loop that computes each frame
			while (unprocessedtime >= UPDATE_CAP) {
				unprocessedtime -= UPDATE_CAP;
				render = true;
				// todo: update game
				game.update(this, (float) UPDATE_CAP);
				System.out.println(input.getMouseX() + ", " + input.getMouseY());

				input.update();

				if (frametime >= 1.0) { // frame time being 1.0 means that one frame has ran once
					frametime = 0;
					fps = frames;
					frames = 0;

				}
			}
			if (render) {
				renderer.clear();

				game.render(this, renderer);
				renderer.process();
				renderer.drawText("Fps: " + fps, 0, 0, 0xff00ffff);
				// window is updated every render
				window.update();
				frames++;
			} else {
				try {
					Thread.sleep(1);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		dispose();

	}

	private void dispose() {

	}

	// getters and setters
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public float getScale() {
		return scale;
	}

	public void setScale(float scale) {
		this.scale = scale;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public window getWindow() {
		return window;
	}

	public input getInput() {
		return input;
	}

}
