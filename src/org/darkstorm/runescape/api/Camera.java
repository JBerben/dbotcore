package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Tile;

public interface Camera extends Utility {
	public enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}

	public double getAngleX();

	public double getAngleY();

	public double getAngleXTo(Tile tile);

	public void setAngleX(double angle);

	public void setAngleXTo(Tile tile);

	public void setAngleY(double angle);

	public void setCompassDirection(Direction direction);
}
