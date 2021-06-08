package com.game.engine;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.game.engine.gfx.font;
import com.game.engine.gfx.image;
import com.game.engine.gfx.imageRequest;
import com.game.engine.gfx.imageTile;
import com.game.engine.gfx.light;
import com.game.engine.gfx.lightRequest;

public class render {
	private ArrayList<imageRequest> imageRequest = new ArrayList<imageRequest>();
	private ArrayList<lightRequest> lightRequest = new ArrayList<lightRequest>();

	private int pW, pH; // pixel width and height
	private int[] pixel;
	private int[] zBuffer;
	private int zDepth = 0;
	private boolean processing = false;
	private int[] lightMap;
	private int[] lightBlock;
	// default color for no light
	private int ambColor = 0xff232323;

	public int getzDepth() {
		return zDepth;
	}

	public void setzDepth(int zDepth) {
		this.zDepth = zDepth;

	}

	private font Font = font.STANDARD;

	public render(engine_ignition ei) {
		pW = ei.getWidth();
		pH = ei.getHeight();

		/*
		 * gives pixel array direct access to the pixel data in the current window now,
		 * we can manipulate the actual image through this pixel array as its an alias
		 * of the actual pixel data
		 */
		pixel = ((DataBufferInt) ei.getWindow().getImage().getRaster().getDataBuffer()).getData();
		zBuffer = new int[pixel.length];
		lightMap = new int[pixel.length];
		lightBlock = new int[pixel.length];

	}

	public void process() {
		processing = true;
		Collections.sort(imageRequest, new Comparator<imageRequest>() {

			@Override
			public int compare(imageRequest ir0, imageRequest ir1) {
				if (ir0.zDepth < ir1.zDepth) {
					return -1;
				}
				if (ir0.zDepth > ir1.zDepth) {
					return 1;
				}

				return 0;
			}

		});
		for (int i = 0; i < imageRequest.size(); i++) {
			imageRequest ir = imageRequest.get(i);
			setzDepth(ir.zDepth);
			drawImage(ir.image, ir.offX, ir.offY);
		}
		// draw lighting
		for (int i = 0; i < lightRequest.size(); i++) {
			lightRequest l = lightRequest.get(i);
			drawLightRequest(l.light, l.locX, l.locY);
		}
		// merges 2 arrays to generate the final light color
		for (int i = 0; i < pixel.length; i++) {

			float red = ((lightMap[i] >> 16) & 0xff) / 255f;
			float green = ((lightMap[i] >> 8) & 0xff) / 255f;
			float blue = (lightMap[i] & 0xff) / 255f;
			pixel[i] = ((int) (((pixel[i] >> 16) & 0xff) * red) << 16 | (int) (((pixel[i] >> 8) & 0xff) * green) << 8
					| (int) ((pixel[i] & 0xff) * blue));

		}
		imageRequest.clear();
		lightRequest.clear();
		processing = false;
	}

	public void clear() {
		for (int i = 0; i < pixel.length; i++) {
			// setting it to 0 means the image in now blank
			pixel[i] = 0;
			zBuffer[i] = 0;
			lightMap[i] = ambColor;
			lightBlock[i] = 0;

		}
	}

	public void setPixel(int x, int y, int value) {
		int alpha = ((value >> 24) & 0xff);
		// checking if pixels are out of bounds
		if (x < 0 || x >= pW || y < 0 || y >= pH || alpha == 0) {
			return;// Doesn't draw anything
		}
		int index = x + y * pW;
		if (zBuffer[index] > zDepth) {
			return;

		}
		zBuffer[index] = zDepth;
		if (alpha == 255) {

			// the [x+y*pW] converts 2d index into a 1d index
			pixel[index] = value;
		} else { // alpha blending
			int pixelColor = pixel[index];
			int newRed = ((pixelColor >> 16) & 0xff)
					- (int) ((((pixelColor >> 16) & 0xff) - ((value >> 16) & 0xff)) * (alpha / 255f));
			int newGreen = ((pixelColor >> 8) & 0xff)
					- (int) ((((pixelColor >> 8) & 0xff) - ((value >> 8) & 0xff)) * (alpha / 255f));
			int newBlue = (pixelColor & 0xff) - (int) (((pixelColor & 0xff) - (value & 0xff)) * (alpha / 255f));

			pixel[index] = (newRed << 16 | newGreen << 8 | newBlue);
		}
	}

	public void setLightBlock(int x, int y, int value) {
		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}
		lightBlock[x + y * pW] = value;
		if (zBuffer[x + y * pW] > zDepth) {
			return;

		}
	}

	public void setLightMap(int x, int y, int value) {
		if (x < 0 || x >= pW || y < 0 || y >= pH) {
			return;
		}
		int baseColor = lightMap[x + y * pW];
		int maxR = Math.max((baseColor >> 16) & 0xff, (value >> 16) & 0xff);
		int maxG = Math.max((baseColor >> 8) & 0xff, (value >> 8) & 0xff);
		int maxB = Math.max(baseColor & 0xff, value & 0xff);
		lightMap[x + y * pW] = (maxR << 16 | maxG << 8 | maxB);

	}

	public void drawText(String text, int offX, int offY, int color) {
		image fontImage = Font.getFontImage();
		int offset = 0;
		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i);
			for (int y = 0; y < fontImage.getHeight(); y++) {
				for (int x = 0; x < Font.getWidths()[unicode]; x++) {
					if (Font.getFontImage().getPixels()[(x + Font.getOffsets()[unicode])
							+ y * Font.getFontImage().getWidth()] == 0xffffffff) {
						setPixel(x + offX + offset, y + offY, color);
					}
				}
			}
			offset += Font.getWidths()[unicode];
		}

	}

	public void drawImage(image image, int offX, int offY) {
		if (image.isAlpha() && !processing) {
			imageRequest.add(new imageRequest(image, zDepth, offX, offY));
			return;
		}
		if (offX < -image.getWidth() || offY < -image.getHeight() || offX >= pW || offY >= pH)
			return;

		int newX = 0, newY = 0;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();

		if (offX < 0)
			newX -= offX;
		if (offY < 0)
			newY -= offY;
		if (newWidth + offX > pW)
			newWidth -= newWidth + (offX - pW);
		if (newHeight + offY > pH)
			newHeight -= newHeight + (offY - pH);

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				// draws a image on the given location
				setPixel(x + offX, y + offY, image.getPixels()[x + y * image.getWidth()]);
				setLightBlock(x + offX, +offY, image.getLightBlock());
			}
		}

	}

	public void drawImageTile(imageTile image, int offX, int offY, int tileX, int tileY) {
		if (image.isAlpha() && !processing) {
			imageRequest.add(new imageRequest(image.getTileImage(tileX, tileY), zDepth, offX, offY));
			return;
		}
		if (offX < -image.getTileW() || offY < -image.getTileH() || offX >= pW || offY >= pH)
			return;
		int newX = 0, newY = 0;
		int newWidth = image.getWidth();
		int newHeight = image.getHeight();

		if (offX < 0)
			newX -= offX;
		if (offY < 0)
			newY -= offY;
		if (newWidth + offX > pW)
			newWidth -= newWidth + (offX - pW);
		if (newHeight + offY > pH)
			newHeight -= newHeight + (offY - pH);

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				// draws a image on the given location
				setPixel(x + offX, y + offY, image.getPixels()[(x + tileX * image.getTileW())
						+ (y + tileY * image.getTileH()) * image.getWidth()]);
				setLightBlock(x + offX, +offY, image.getLightBlock());

			}
		}
	}

	public void drawRect(int offX, int offY, int width, int height, int color) {

		for (int y = 0; y <= width; y++) {
			setPixel(offX, y + offY, color);
			setPixel(offX + width, y + offY, color);

		}
		for (int x = 0; x <= width; x++) {
			setPixel(x + offX, offY, color);
			setPixel(x + offX, offY + height, color);
		}
	}

	public void drawFillRect(int offX, int offY, int width, int height, int color) {
		if (offX < -width || offY < -height || offX >= pW || offY >= pH)
			return;
		int newX = 0, newY = 0;
		int newWidth = width;
		int newHeight = height;

		if (offX < 0)
			newX -= offX;
		if (offY < 0)
			newY -= offY;
		if (newWidth + offX > pW)
			newWidth -= newWidth + (offX - pW);
		if (newHeight + offY > pH)
			newHeight -= newHeight + (offY - pH);

		for (int y = newY; y < newHeight; y++) {
			for (int x = newX; x < newWidth; x++) {
				setPixel(x + offX, y + offY, color);

			}
		}
	}

	public void drawLight(light l, int offX, int offY) {
		lightRequest.add(new lightRequest(l, offX, offY));
	}

	private void drawLightRequest(light l, int offX, int offY) {

		for (int i = 0; i <= l.getDiameter(); i++) {
			drawLightLine(l, l.getRadius(), l.getRadius(), i, 0, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), i, l.getDiameter(), offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), 0, i, offX, offY);
			drawLightLine(l, l.getRadius(), l.getRadius(), l.getDiameter(), i, offX, offY);

		}
	}

	private void drawLightLine(light l, int x0, int y0, int x1, int y1, int offX, int offY) {
		// vector length
		int dx = Math.abs(x1 - x0);
		int dy = Math.abs(y1 - y0);

		// find out the quadrant
		int sx = x0 < x1 ? 1 : -1;
		int sy = y0 < y1 ? 1 : -1;

		int err = dx - dy;
		int e2;
		while (true) {
			int screenX = x0 - l.getRadius() + offX;
			int screenY = y0 - l.getRadius() + offY;

			if (screenX < 0 || screenX >= pW || screenY < 0 || screenY >= pH)
				return;

			int lightColor = l.getLightValue(x0, y0);
			if (lightColor == 0)
				return;
			// place shadow if another object in path
			if (lightBlock[screenX + screenY * pW] == light.FULL)
				return;
			setLightMap(screenX, screenY, lightColor);
			if (x0 == x1 && y0 == y1) {
				break;
			}
			e2 = 2 * err;
			if (e2 > -1 * dy) {
				err -= dy;
				x0 += sx;
			}
			if (e2 < dx) {
				err += dx;
				y0 += sy;
			}
		}

	}
}
