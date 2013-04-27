package org.darkstorm.runescape.api;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.input.MouseTarget;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface Walking extends Utility {
	public static enum WalkMode {
		MINIMAP,
		SCREEN,
		EITHER
	}

	public MouseTarget getMinimapTileTarget(Tile tile);

	public boolean walkPath(TilePath path);

	public boolean walkPath(TilePath path, WalkMode mode);

	public boolean walkTo(Tile destination);

	public boolean walkTo(Tile destination, WalkMode mode);

	public boolean canReach(Locatable target);

	public double distanceTo(Locatable target);

	public boolean isRunning();

	public void setRunning(boolean running);

	@GameTypeSupport(GameType.CURRENT)
	public boolean isResting();

	@GameTypeSupport(GameType.CURRENT)
	public void setResting(boolean resting);

	public int getRunEnergy();

	public boolean hasMinimapDestination();

	public Tile getMinimapDestination();

	public boolean isOnMinimap(Tile tile);

	public InterfaceComponent getRunComponent();
}
