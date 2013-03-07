package org.darkstorm.runescape.api.util;

public enum StandardSpells implements Spell {
	AIR_STRIKE("Air Strike", 0),
	WATER_STRIKE("Water Strike", 5),
	EARTH_STRIKE("Earth Strike", 9),
	FIRE_STRIKE("Fire Strike", 13),

	AIR_BOLT("Air Bolt", 17),
	WATER_BOLT("Water Bolt", 23),
	EARTH_BOLT("Earth Bolt", 29),
	FIRE_BOLT("Fire Bolt", 35),

	AIR_BLAST("Air Blast", 41),
	WATER_BLAST("Water Blast", 47),
	EARTH_BLAST("Earth Blast", 53),
	FIRE_BLAST("Fire Blast", 59),

	AIR_WAVE("Air Wave", 62),
	WATER_WAVE("Water Wave", 65),
	EARTH_WAVE("Earth Wave", 70),
	FIRE_WAVE("Fire Wave", 75),

	AIR_SURGE("Air Surge", 81),
	WATER_SURGE("Water Surge", 85),
	EARTH_SURGE("Earth Surge", 90),
	FIRE_SURGE("Fire Surge", 95),

	SLAYER_DART("Slayer Dart", 50),
	DIVINE_STORM("Divine Storm", 60),
	STORM_OF_ARMADYL("Storm of Armadyl", 77),
	POLYPORE_STRIKE("Polypore Strike", 80),

	CONFUSE("Confuse", 3),
	WEAKEN("Weaken", 11),
	CURSE("Curse", 19),
	BIND("Bind", 20),
	SNARE("Snare", 50),
	VULNERABILITY("Vulnerability", 66),
	ENFEEBLE("Enfeeble", 73),
	ENTANGLE("Entangle", 79),
	STAGGER("Stagger", 80),

	HOME_TELEPORT("Home Teleport", 0),
	MOBILISING_ARMIES_TELEPORT("Mobilising Armies Teleport", 10),
	VARROCK_TELEPORT("Varrock Teleport", 25),
	LUMBRIDGE_TELEPORT("Lumbridge Teleport", 31),
	FALADOR_TELEPORT("Falador Teleport", 37),
	HOUSE_TELEPORT("Teleport to House", 40),
	CAMELOT_TELEPORT("Camelot Teleport", 45),
	ARDOUGNE_TELEPORT("Ardougne Teleport", 51),
	WATCHTOWER_TELEPORT("Watchtower Teleport", 58),
	TROLLHEIM_TELEPORT("Trollheim Teleport", 61),
	APE_ATOLL_TELEPORT("Teleport to Ape Atoll", 64),

	TELEOTHER_LUMBRIDGE("Teleother Lumb", 82),
	TELEOTHER_FALADOR("Teleother Falador", 82),
	TELEOTHER_CAMELOT("Teleother Camelot", 90),

	TELEKINETIC_GRAB("Telekinetic Grab", 33),
	TELEPORT_BLOCK("Teleport Block", 85),

	ENCHANT_CROSSBOW_BOLT("Enchant Crossbow Bolt", 4),
	ENCHANT_LEVEL_1_JEWELLERY("Enchant Level 1 Jewellery", 7),
	ENCHANT_LEVEL_2_JEWELLERY("Enchant Level 2 Jewellery", 27),
	ENCHANT_LEVEL_3_JEWELLERY("Enchant Level 3 Jewellery", 49),
	ENCHANT_LEVEL_4_JEWELLERY("Enchant Level 4 Jewellery", 57),
	ENCHANT_LEVEL_5_JEWELLERY("Enchant Level 5 Jewellery", 68),
	ENCHANT_LEVEL_6_JEWELLERY("Enchant Level 6 Jewellery", 87),

	BONES_TO_BANANAS("Bones to Bananas", 15),
	BONES_TO_PEACHES("Bones to Peaches", 60),
	LOW_LEVEL_ALCHEMY("Low Level Alchemy", 21),
	HIGH_LEVEL_ALCHEMY("High Level Alchemy", 55),
	SUPERHEAT_ITEM("Superheat Item", 43),

	CHARGE_AIR_ORB("Charge Air Orb", 66),
	CHARGE_WATER_ORB("Charge Water Orb", 56),
	CHARGE_EARTH_ORB("Charge Earth Orb", 60),
	CHARGE_FIRE_ORB("Charge Fire Orb", 63);

	private final String name;
	private final int requiredLevel;

	private StandardSpells(String name, int requiredLevel) {
		this.name = name;
		this.requiredLevel = requiredLevel;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int getRequiredLevel() {
		return requiredLevel;
	}

	@Override
	public Spellbook getSpellbook() {
		return Spellbook.STANDARD;
	}
}