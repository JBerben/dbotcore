package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface CombatTab extends Tab {
	@GameTypeSupport(GameType.OLDSCHOOL)
	public String[] getStyles();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public String getSelectedStyle();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public void selectStyle(String style);

	public boolean isAutoRetaliating();

	public void setAutoRetaliating(boolean autoretaliate);

	public int getCombatLevel();
}
