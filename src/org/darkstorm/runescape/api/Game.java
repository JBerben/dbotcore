package org.darkstorm.runescape.api;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.tab.Tab;
import org.darkstorm.runescape.api.util.Tile;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface Game extends Utility {
	public Tab[] getTabs();

	public Tab getOpenTab();

	public Tab getTab(String name);

	public Tab getTab(Class<? extends Tab> tabClass);

	public void openTab(Tab tab);

	public int getSelectedTabIndex();

	@GameTypeSupport(GameType.CURRENT)
	public void closeCurrentTab();

	public int getHealthPercentage();

	public int getHealth();

	public int getMaxHealth();

	public int getRunPercentage();

	public int getPrayerPercentage();

	public int getPrayerPoints();

	public int getMaxPrayerPoints();

	@GameTypeSupport(GameType.CURRENT)
	public void enableQuickPrayers();

	@GameTypeSupport(GameType.CURRENT)
	public void disableQuickPrayers();

	@GameTypeSupport(GameType.CURRENT)
	public boolean isUsingQuickPrayers();

	public boolean hasSelectedItem();

	public boolean hasDestination();

	public Tile getDestination();

	public int getCurrentFloor();

	public int getGameState();

	@GameTypeSupport(GameType.CURRENT)
	public boolean isInFixedMode();

	public boolean isLoading();

	public boolean isLoggedIn();

	@GameTypeSupport(GameType.CURRENT)
	public boolean isInLobby();

	public void logout();

	public void logout(@GameTypeSupport(GameType.CURRENT) boolean toLobby);
}
