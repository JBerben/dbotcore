package org.darkstorm.runescape.api;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.input.MouseTarget;
import org.darkstorm.runescape.api.tab.SettingsTab;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface Walking extends Utility, SettingsTab {
	public MouseTarget getMinimapTileTarget(Tile tile);

	public void clickMinimapTile(Tile tile);

	public void walkPath(Tile[] path);

	public void walkTo(Tile destination);

	public Tile[] generatePath(Tile destination);

	@Override
	public boolean isRunning();

	@Override
	public void setRunning(boolean running);

	@GameTypeSupport(GameType.CURRENT)
	public boolean isResting();

	@GameTypeSupport(GameType.CURRENT)
	public void setResting(boolean resting);

	public int getRunEnergy();

	public Tile[] reversePath(Tile[] path);

	public Tile[] randomizePath(Tile[] path, double factor);

	public boolean hasMinimapDestination();

	public Tile getMinimapDestination();

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public String getName();

	@Override
	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getButtonComponent();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public InterfaceComponent getRunComponent();
}
