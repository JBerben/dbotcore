package org.darkstorm.runescape.api.util;

public final class Tile implements Locatable, Cloneable {
	private final double x, y;
	private final int plane;

	public Tile(int x, int y) {
		this(x, y, 0);
	}

	public Tile(double x, double y) {
		this(x, y, 0);
	}

	public Tile(Tile tile) {
		this(tile.getPreciseX(), tile.getPreciseY(), tile.getPlane());
	}

	public Tile(int x, int y, int plane) {
		this(x + 0.5, y + 0.5, plane);
	}

	public Tile(double x, double y, int plane) {
		this.x = x;
		this.y = y;
		this.plane = plane;
	}

	public int getX() {
		return (int) x;
	}

	public int getY() {
		return (int) y;
	}

	public double getPreciseX() {
		return x;
	}

	public double getPreciseY() {
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
		return Math.hypot(x - tile.x, y - tile.y);
	}

	public double distanceToSquared(Locatable locatable) {
		Tile tile = locatable.getLocation();
		return Math.pow(x - tile.x, 2) + Math.pow(y - tile.y, 2);
	}

	public Tile randomize(double maxDistance) {
		double radius = Math.random() * maxDistance;
		double xOffset = Math.cos(Math.toRadians(Math.random() * 360)) * radius;
		double yOffset = Math.sin(Math.toRadians(Math.random() * 360)) * radius;
		return new Tile(x + xOffset, y + yOffset, plane);
	}

	public Tile randomize(double xOffset, double yOffset) {
		xOffset = xOffset / 2D - Math.random() * xOffset;
		yOffset = yOffset / 2D - Math.random() * yOffset;
		return new Tile(x + xOffset, y + yOffset, plane);
	}

	public Tile offset(double xOffset, double yOffset) {
		return new Tile(x + xOffset, y + yOffset);
	}

	@Override
	public String toString() {
		return "Tile{x=" + x + ",y=" + y + ",plane=" + plane + "}";
	}

	@Override
	public int hashCode() {
		return (x + "," + y + "," + plane).hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Tile))
			return false;
		Tile tile = (Tile) obj;
		return tile.x == x && tile.y == y && tile.plane == plane;
	}
}
