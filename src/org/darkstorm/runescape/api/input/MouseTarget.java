package org.darkstorm.runescape.api.input;

import java.awt.Point;

public interface MouseTarget {
	public Point getLocation();

	public boolean isOver(Point point);
}
