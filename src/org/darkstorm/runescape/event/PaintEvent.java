package org.darkstorm.runescape.event;

import java.awt.Graphics;

public class PaintEvent extends Event {
	private final Graphics graphics;
	private final int width, height;

	public PaintEvent(Graphics graphics, int width, int height) {
		this.graphics = graphics;
		this.width = width;
		this.height = height;
	}

	public Graphics getGraphics() {
		return graphics.create();
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
}
