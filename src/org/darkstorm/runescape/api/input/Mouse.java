package org.darkstorm.runescape.api.input;

import java.awt.Point;

import org.darkstorm.runescape.api.*;

public interface Mouse extends Utility {
	public Point getLocation();

	public void hover(MouseTargetable target);

	public void move(MouseTargetable target);

	public void click(MouseTargetable target);

	public void click(MouseTargetable target, boolean left);

	public void moveRandomly(int maximumDeviation);

	public void click(boolean left);

	public boolean isActive();

	public void stop();

	public boolean await();

	public boolean await(int timeout);

	public boolean isSynchronous();

	public void setSynchronous(boolean synchronous);

	@Override
	public GameContext getContext();
}
