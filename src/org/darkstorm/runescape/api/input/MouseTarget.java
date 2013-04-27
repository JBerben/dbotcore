package org.darkstorm.runescape.api.input;

import java.awt.*;

public interface MouseTarget extends MouseTargetable {
	public Point getLocation();

	public boolean isOver(Point point);

	public void render(Graphics g);
}
