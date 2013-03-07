package org.darkstorm.runescape.api.util;

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

	public double distanceTo(Locatable locatable) {
		Tile tile = locatable.getLocation();
		return Math.sqrt(Math.pow(x - tile.getX(), 2)
				+ Math.pow(y - tile.getY(), 2));
	}

	@Override
	public String toString() {
		return "Tile{x=" + x + ",y=" + y + ",plane=" + plane + "}";
	}
}
