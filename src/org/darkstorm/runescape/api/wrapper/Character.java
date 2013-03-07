package org.darkstorm.runescape.api.wrapper;

import java.awt.Point;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;

public interface Character extends Animable, MouseTargetable, Locatable,
		Wrapper {
	@Override
	public Tile getLocation();

	public Tile[] getWaypoints();

	public String getOverheadMessage();

	public double getRotation();

	public int getAnimation();

	public boolean isHitting();

	public boolean isInCombat();

	public int getMotion();

	public boolean isMoving();

	public Character getInteractionTarget();

	public boolean isOnScreen();

	public Point getScreenLocation();

	@Override
	public MouseTarget getTarget();
}
