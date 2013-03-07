package org.darkstorm.runescape.api.wrapper;

public interface Player extends Character, Wrapper {
	public int getTeam();

	public int[] getAppearance();
}
