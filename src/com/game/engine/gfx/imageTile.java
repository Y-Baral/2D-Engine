package com.game.engine.gfx;

public class imageTile extends image {
	private int tileW, tileH;

	public imageTile(String path, int tileW, int tileH) {
		super(path);
		this.tileW = tileW;
		this.tileH = tileH;
	}

	public image getTileImage(int tileX, int tileY) {
		int[] p = new int[tileW * tileH];
		for (int x = 0; x < tileW; x++) {
			for (int y = 0; y < tileH; y++) {
				p[x + y * tileW] = this.getPixels()[(x + tileX * tileW) + (y + tileY * tileH) * this.getWidth()];
			}
		}
		return new image(p, tileW, tileH);
	}

	public int getTileW() {
		return tileW;
	}

	public void setTileW(int tileW) {
		this.tileW = tileW;
	}

	public int getTileH() {
		return tileH;
	}

	public void setTileH(int tileH) {
		this.tileH = tileH;
	}
}
