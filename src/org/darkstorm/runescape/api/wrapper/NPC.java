package org.darkstorm.runescape.api.wrapper;

public interface NPC extends Character {
	public String getName();

	public int getId();

	public int getLevel();

	public int getPrayerIcon();

	public String[] getActions();

	public Model getModel();
}
