package com.game.engine.gfx;


public class imageRequest {
	public image image;
	public int zDepth;
	public int offX, offY;

	public imageRequest(image image, int zDepth, int offX, int offY) {
		this.image = image;
		this.zDepth = zDepth;
		this.offX = offX;
		this.offY = offY;

	}
}
