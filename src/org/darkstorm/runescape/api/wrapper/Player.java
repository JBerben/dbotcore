package org.darkstorm.runescape.api.wrapper;

public interface Player extends Character, Wrapper {
	public int getTeam();

	public int[] getAppearance();

	public int[] getColor();

	public boolean isMale();

	public int getPrayerIcon();

	public int getSkullIcon();

	public int getNpcId();
}
