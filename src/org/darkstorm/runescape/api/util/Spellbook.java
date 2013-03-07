package org.darkstorm.runescape.api.util;

public enum Spellbook {
	STANDARD("Standard Spellbook") {
		@Override
		public Spell[] getSpells() {
			return StandardSpells.values();
		}
	},
	ANCIENT("Ancient Magicks") {
		@Override
		public Spell[] getSpells() {
			return AncientSpells.values();
		}
	},
	LUNAR("Lunar Spellbook") {
		@Override
		public Spell[] getSpells() {
			return LunarSpells.values();
		}
	};

	private final String name;

	private Spellbook(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract Spell[] getSpells();
}