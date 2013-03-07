package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.util.Tile;

public interface GroundItem extends MouseTargetable, Wrapper {
	public int getId();

	public int getStackSize();

	public Tile getLocation();

	@Override
	public MouseTarget getTarget();
}
