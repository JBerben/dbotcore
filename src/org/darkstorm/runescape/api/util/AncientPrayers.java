package org.darkstorm.runescape.api.util;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.util.GameTypeSupport;

@GameTypeSupport(GameType.CURRENT)
public enum AncientPrayers implements Prayer {
	PROTECT_ITEM("Protect Item", 50),
	SAP_WARRIOR("Sap Warrior", 50),
	SAP_RANGER("Sap Ranger", 52),
	SAP_RANGE_STRENGTH("Sap Range Strength", 53),
	SAP_MAGE("Sap Mage", 54),
	SAP_MAGIC_STRENGTH("Sap Magic Strength", 55),
	SAP_SPIRIT("Sap Spirit", 56),
	SAP_DEFENCE("Sap Defence", 57),
	SAP_STRENGTH("Sap Strength", 58),
	BERSERKER("Berserker", 59),
	DEFLECT_SUMMONING("Deflect Summoning", 62),
	DEFLECT_MAGIC("Deflect Magic", 65),
	DEFLECT_MISSILES("Deflect Missiles", 68),
	DEFLECT_MELEE("Deflect Melee", 71),
	LEECH_ATTACK("Leech Attack", 74),
	LEECH_RANGED("Leech Ranged", 76),
	LEECH_RANGE_STRENGTH("Leech Range Strength", 77),
	LEECH_MAGIC("Leech Magic", 78),
	LEECH_MAGIC_STRENGTH("Leech Magic Strength", 79),
	LEECH_DEFENCE("Leech Defence", 80),
	LEECH_STRENGTH("Leech Strength", 82),
	LEECH_ENERGY("Leech Energy", 84),
	LEECH_ADRENALINE("Leech Adrenaline", 86),
	WRATH("Wrath", 89),
	SOUL_SPLIT("Soul Split", 92),
	TURMOIL("Turmoil", 95),
	ANGUISH("Anguish", 95),
	TORMENT("Torment", 95);

	private final String name;
	private final int level;

	private AncientPrayers(String name, int level) {
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
