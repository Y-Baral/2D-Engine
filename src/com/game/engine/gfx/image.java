package com.game.engine.gfx;

import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

public class image {
	private int width;
	private int height;
	private int[] pixels;
	private boolean alpha = false;
	private int lightBlock = light.NONE;

	public image(String path) {
		BufferedImage image = null;
		try {
			image = ImageIO.read(image.class.getResourceAsStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		width = image.getWidth();
		height = image.getHeight();
		pixels = image.getRGB(0, 0, width, height, null, 0, width);

		image.flush();

	}

	public image(int[] p, int w, int h) {
		this.pixels = p;
		this.width = w;
		this.height = h;
	}

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

	public int[] getPixels() {
		return pixels;
	}

	public void setPixels(int[] pixels) {
		this.pixels = pixels;
	}

	public int getLightBlock() {
		return lightBlock;
	}

	public void setLightBlock(int lightBlock) {
		this.lightBlock = lightBlock;
	}

	public boolean isAlpha() {
		return alpha;
	}

	public void setAlpha(boolean alpha) {
		this.alpha = alpha;
	}

}