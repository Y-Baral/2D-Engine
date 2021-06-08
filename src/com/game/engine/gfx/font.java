package com.game.engine.gfx;

import com.game.engine.gfx.font;
import com.game.engine.gfx.image;

public class font {
	public static final font STANDARD = new font("/fonts/standard.png");
	private image fontImage;
	// parallel arrays to store individual widths of each characters
	private int[] offsets;
	private int[] widths;

	public font(String path) {
		fontImage = new image(path);
		offsets = new int[256];
		widths = new int[256];
		int unicode = 0;
		// marks end of width in the font image
		float hexcode1 = 0xff0000ff;

		// takes in a picture and splits it into characters ready to be displayed
		for (int i = 0; i < fontImage.getWidth(); i++) {
			if (fontImage.getPixels()[i] == hexcode1) {
				offsets[unicode] = i;
			}
			float hexcode2 = 0xffffff00;
			if (fontImage.getPixels()[i] == hexcode2) {
				widths[unicode] = i - offsets[unicode];
				unicode++;
			}

		}
	}

	public image getFontImage() {
		return fontImage;
	}

	public void setFontImage(image fontImage) {
		this.fontImage = fontImage;
	}

	public int[] getOffsets() {
		return offsets;
	}

	public void setOffsets(int[] offsets) {
		this.offsets = offsets;
	}

	public int[] getWidths() {
		return widths;
	}

	public void setWidths(int[] widths) {
		this.widths = widths;
	}
}
