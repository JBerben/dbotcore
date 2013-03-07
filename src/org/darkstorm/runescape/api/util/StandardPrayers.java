package org.darkstorm.runescape.api.util;

public enum StandardPrayers implements Prayer {
	THICK_SKIN("Thick Skin", 1),
	BURST_OF_STRENGTH("Burst of Strength", 4),
	CLARITY_OF_THOUGHT("Clarity of Thought", 7),
	SHARP_EYE("Sharp Eye", 8),
	UNSTOPPABLE_FORCE("Unstoppable Force", 8),
	MYSTIC_WILL("Mystic Will", 9),
	CHARGE("Charge", 9),
	ROCK_SKIN("Rock Skin", 10),
	SUPERHUMAN_STRENGTH("Superhuman Strength", 13),
	IMPROVED_REFLEXES("Improved Reflexes", 16),
	RAPID_RESTORE("Rapid Restore", 19),
	RAPID_HEAL("Rapid Heal", 22),
	PROTECT_ITEM("Protect Item", 25),
	HAWK_EYE("Hawk Eye", 26),
	UNRELENTING_FORCE("Unrelenting Force", 26),
	MYSTIC_LORE("Mystic Lore", 27),
	SUPER_CHARGE("Super Charge", 27),
	STEEL_SKIN("Steel Skin", 28),
	ULTIMATE_STRENGTH("Ultimate Strength", 31),
	INCREDIBLE_REFLEXES("Incredible Reflexes", 34),
	PROTECT_FROM_SUMMONING("Protect from Summoning", 35),
	PROTECT_FROM_MAGIC("Protect from Magic", 37),
	PROTECT_FROM_MISSILES("Protect from Missiles", 40),
	PROTECT_FROM_MELEE("Protect from Melee", 43),
	EAGLE_EYE("Eagle Eye", 44),
	OVERPOWERING_FORCE("Overpowering Force", 44),
	MYSTIC_MIGHT("Mystic Might", 45),
	OVERCHARGE("Overcharge", 45),
	RETRIBUTION("Retribution", 46),
	REDEMPTION("Redemption", 49),
	SMITE("Smite", 52),
	CHIVALRY("Chivalry", 60),
	RAPID_RENEWAL("Rapid Renewal", 65),
	PIETY("Piety", 70),
	RIGOUR("Rigour", 70),
	AUGURY("Augury", 70);

	private final String name;
	private final int level;

	private StandardPrayers(String name, int level) {
		this.name = name;
		this.level = level;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRequiredLevel() {
		return level;
	}
}
