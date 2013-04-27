package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface CombatTab extends Tab {
	public static interface CombatStyle {
		public String getName();

		public int getIndex();

		public int getTextureId();

		public String[] getDescriptions();
	}

	@GameTypeSupport(GameType.OLDSCHOOL)
	public CombatStyle[] getStyles();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public CombatStyle getSelectedStyle();

	@GameTypeSupport(GameType.OLDSCHOOL)
	public void selectStyle(CombatStyle style);

	public boolean isAutoRetaliating();

	public void setAutoRetaliating(boolean autoRetaliate);

	public int getCombatLevel();

	public String getCurrentWeaponName();

	public boolean hasSpecialAttack();

	public int getSpecialAttackPercentage();

	public void performSpecialAttack();
}
