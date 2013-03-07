package org.darkstorm.runescape.api.util;

import org.darkstorm.runescape.api.wrapper.Locatable;

public final class Tile implements Locatable, Cloneable {
	private final int x, y, plane;

	public Tile(int x, int y) {
		this(x, y, 0);
	}

	public Tile(Tile tile) {
		this(tile.getX(), tile.getY(), tile.getPlane());
	}

	public Tile(int x, int y, int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getPlane() {
		return plane;
	}

	@Override
	public Tile clone() {
		return new Tile(this);
	}

	@Override
	public Tile getLocation() {
		return this;
	}

	@Override
	public String toString() {
		return "Tile{x=" + x + ",y=" + y + ",plane=" + plane + "}";
	}
}
