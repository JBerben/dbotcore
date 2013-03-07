package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;

public interface MagicTab extends Tab {
	public Spellbook getSpellbook();

	public Spell getSelectedSpell();

	public void selectSpell(Spell spell);

	public boolean canCast(Spell spell);

	public Item[] getRequiredItems(Spell spell);

	public InterfaceComponent getComponent(Spell spell);
}
