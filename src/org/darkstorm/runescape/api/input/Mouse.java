package org.darkstorm.runescape.api.input;

import java.awt.Point;

public interface Mouse {
	public Point getLocation();

	public void hover(MouseTargetable target);

	public void hover(MouseTargetable target, int timeout);

	public void move(MouseTargetable target);

	public void click(MouseTargetable target);

	public void click(MouseTargetable target, boolean left);

	public boolean isActive();

	public void stop();

	public boolean await();

	public boolean await(int timeout);
}
