package org.darkstorm.runescape.api.wrapper;

import java.awt.Point;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;

public interface GameObject extends MouseTargetable, Locatable, Wrapper {
	public enum GameObjectType {
		INTERACTIVE,
		DECORATION,
		BOUNDARY,
		OTHER
	}

	public GameObjectType getType();

	public int getId();

	@Override
	public MouseTarget getTarget();

	public boolean isOnScreen();

	public Point getScreenLocation();

	public Model getModel();

	@Override
	public Tile getLocation();
}
