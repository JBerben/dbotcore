package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface PrayerTab extends Tab {
	public boolean isPraying();

	public PrayerSet getPrayerSet();

	public Prayer[] getEnabledPrayers();

	public boolean isPraying(Prayer prayer);

	public boolean canPray(Prayer prayer);

	public void disablePrayers();

	public void setPraying(Prayer prayer, boolean praying);

	@GameTypeSupport(GameType.CURRENT)
	public Prayer[] getQuickPrayers();

	@GameTypeSupport(GameType.CURRENT)
	public void setQuickPrayers(Prayer... prayers);

	@GameTypeSupport(GameType.CURRENT)
	public boolean isSelectingQuickPrayers();

	@GameTypeSupport(GameType.CURRENT)
	public void completeQuickPrayerSelection();

	public InterfaceComponent getComponent(Prayer prayer);
}
