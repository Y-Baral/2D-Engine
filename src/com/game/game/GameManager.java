package com.game.game;

import java.awt.event.KeyEvent;

import com.game.engine.audio.sound_clip;
import com.game.engine.abstractGame;
import com.game.engine.engine_ignition;
import com.game.engine.render;
import com.game.engine.gfx.image;
import com.game.engine.gfx.imageTile;

public class GameManager extends abstractGame {
	private image image1;
	private imageTile image2;
	private sound_clip clip;

	public GameManager() {
		image1 = new image("/test.png");
		image2 = new imageTile("/test3.png", 16, 16);
		image2.setAlpha(true);
		clip = new sound_clip("/audio/test.wav");

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
		renderer.setzDepth(1);
		renderer.drawImageTile(image2, ei.getInput().getMouseX(), ei.getInput().getMouseY(), 1, 1);
		renderer.setzDepth(0);
		renderer.drawImage(image1, 10, 10);
	}

	public static void main(String[] args) {
		// engine gets started with a instance of the GameManager class
		engine_ignition ei = new engine_ignition(new GameManager());
		ei.start();

	}

}
