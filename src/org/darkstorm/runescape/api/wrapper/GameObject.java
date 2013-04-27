package org.darkstorm.runescape.api.wrapper;

import java.awt.Point;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.*;

public interface GameObject extends MouseTargetable, Locatable,
		ScreenLocatable, Wrapper {
	public enum GameObjectType {
		INTERACTIVE,
		DECORATION,
		GROUND,
		BOUNDARY,
		OTHER
	}

	public GameObjectType getType();

	public int getId();

	@Override
	public MouseTarget getTarget();

	@Override
	public boolean isOnScreen();

	@Override
	public Point getScreenLocation();

	public Model getModel();

	@Override
	public Tile getLocation();
}
