package com.game.game;

import java.awt.event.KeyEvent;

import com.game.engine.abstractGame;
import com.game.engine.engine_ignition;
import com.game.engine.render;
import com.game.engine.audio.sound_clip;
import com.game.engine.gfx.image;
import com.game.engine.gfx.imageTile;
import com.game.engine.gfx.light;

public class GameManager extends abstractGame {
	private image image1;
	private imageTile image2;
	private sound_clip clip;
	private light light;

	public GameManager() {
		image1 = new image("/test4.png");
		image1.setAlpha(true);
		image1.setLightBlock(light.FULL);
		image2 = new imageTile("/test2.png", 16, 16);
		image2.setAlpha(false);
		clip = new sound_clip("/audio/test.wav");
		light = new light(100, 0xff00ffff);

	}

	@Override
	public void update(engine_ignition ei, float delta_time) {
		// TODO Auto-generated method stub
		if (ei.getInput().isKeyDown(KeyEvent.VK_A)) {
			System.out.print("rbrwihbwribribrivbr");
			clip.play();
			clip.setVolume(-20);
		}
	}

	float temp = 0;

	@Override
	public void render(engine_ignition ei, render renderer) {

		renderer.setzDepth(0);
		renderer.drawImage(image2, 0, 0);
		renderer.drawImage(image1, 80, 30);
		renderer.drawLight(light, ei.getInput().getMouseX(), ei.getInput().getMouseY());

	}

	public static void main(String[] args) {
		// engine gets started with a instance of the GameManager class
		engine_ignition ei = new engine_ignition(new GameManager());
		ei.setWidth(320);
		ei.setWidth(240);
		ei.setScale(5f);
		ei.start();

	}

}
