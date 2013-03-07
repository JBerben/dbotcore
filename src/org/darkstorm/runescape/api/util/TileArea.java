package org.darkstorm.runescape.api.util;

public final class TileArea implements Cloneable {
	private final int x, y, width, height;
	private final Tile origin, center, endpoint;

	public TileArea(Tile corner1, Tile corner2) {
		this(Math.min(corner1.getX(), corner2.getX()), Math.min(corner1.getY(),
				corner2.getY()), Math.max(corner1.getX(), corner2.getX())
				- Math.min(corner1.getX(), corner2.getX()), Math.max(
				corner1.getY(), corner2.getY())
				- Math.min(corner1.getY(), corner2.getY()));
	}

	public TileArea(Tile origin, int width, int height) {
		this(origin.getX(), origin.getY(), width, height);
	}

	public TileArea(TileArea area) {
		this(area.getX(), area.getY(), area.getWidth(), area.getHeight());
	}

	public TileArea(int x, int y, int width, int height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;

		origin = new Tile(x, y);
		center = new Tile(x + width / 2, y + height / 2);
		endpoint = new Tile(x + width, y + height);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Tile getOrigin() {
		return origin;
	}

	public Tile getCenter() {
		return center;
	}

	public Tile getEndpoint() {
		return endpoint;
	}

	public boolean contains(Tile tile) {
		return contains(tile.getX(), tile.getY());
	}

	public boolean contains(int x, int y) {
		return x >= this.x && y >= this.y && x < this.x + width
				&& y < this.y + height;
	}

	@Override
	public TileArea clone() {
		return new TileArea(this);
	}

	@Override
	public String toString() {
		return "TileArea{x=" + x + ",y=" + y + ",w=" + width + ",h=" + height
				+ "}";
	}
}
