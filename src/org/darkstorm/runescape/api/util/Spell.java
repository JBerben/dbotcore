package org.darkstorm.runescape.api.util;

public interface Spell {
	public String getName();

	public Spellbook getSpellbook();

	public int getRequiredLevel();
}