package com.game.engine;

import java.awt.image.DataBufferInt;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.game.engine.gfx.font;
import com.game.engine.gfx.image;
import com.game.engine.gfx.imageRequest;
import com.game.engine.gfx.imageTile;

public class render {
	private ArrayList<imageRequest> imageRequest = new ArrayList<imageRequest>();
	private int pW, pH; // pixel width and height
	private int[] pixel;
	private int[] zBuffer;
	private int zDepth = 0;
	private boolean processing = false;

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
		imageRequest.clear();
		processing = false;
	}

	public void clear() {
		for (int i = 0; i < pixel.length; i++) {
			// setting it to 0 means the image in now blank
			pixel[i] = 0;
			zBuffer[i] = 0;

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

			pixel[index] = (255 << 24 | newRed << 16 | newGreen << 8 | newBlue);
		}
	}

	public void drawText(String text, int offX, int offY, int color) {
		image fontImage = Font.getFontImage();
		text = text.toUpperCase();
		int offset = 0;
		for (int i = 0; i < text.length(); i++) {
			int unicode = text.codePointAt(i) - 32;// -32 to negate other unicode before space
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
}
