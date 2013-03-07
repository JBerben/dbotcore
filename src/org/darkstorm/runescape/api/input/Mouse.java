package org.darkstorm.runescape.api.input;

import java.awt.Point;

public interface Mouse {
	public Point getLocation();

	public void hover(MouseTarget target);

	public void hover(MouseTarget target, int timeout);

	public void move(MouseTarget target);

	public void click(MouseTarget target);

	public void click(MouseTarget target, boolean left);

	public boolean isActive();

	public void stop();

	public boolean await();

	public boolean await(int timeout);
}
