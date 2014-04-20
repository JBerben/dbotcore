package org.darkstorm.runescape.oldschool.overrides;

import java.awt.*;
import java.awt.image.BufferedImage;

@SuppressWarnings("serial")
public abstract class GameCanvas extends Canvas {
	// private OldSchoolBot bot;
	//
	// @Override
	// public Graphics getGraphics() {
	// return super.getGraphics();
	// }

	@Override
	public Image createImage(int width, int height) {
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	}
}
