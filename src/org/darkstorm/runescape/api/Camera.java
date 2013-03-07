package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Locatable;

public interface Camera extends Utility {
	public enum Direction {
		NORTH,
		SOUTH,
		EAST,
		WEST
	}

	public double getAngleX();

	public double getAngleY();

	public double getAngleXTo(Locatable locatable);

	public void setAngleX(double angle);

	public void setAngleXTo(Locatable locatable);

	public void setAngleY(double angle);

	public void turnTo(Locatable locatable);

	public void setCompassDirection(Direction direction);
}
