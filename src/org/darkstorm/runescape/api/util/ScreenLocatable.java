package org.darkstorm.runescape.api.util;

import java.awt.Point;

public interface ScreenLocatable {
	public boolean isOnScreen();

	public Point getScreenLocation();
}
