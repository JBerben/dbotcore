package org.darkstorm.runescape.api.util;

public enum LunarSpells implements Spell {
	HOME_TELEPORT("Home Teleport", 0),
	MOONCLAN_TELEPORT("Moonclan Teleport", 69),
	OURANIA_TELEPORT("Ourania Teleport", 71),
	WATERBIRTH_TELEPORT("Waterbirth Teleport", 72),
	SOUTH_FALADOR_TELEPORT("South Falador Teleport", 72),
	BARBARIAN_TELEPORT("Barbarian Teleport", 75),
	NORTH_ARDOUGNE_TELEPORT("North Ardougne Teleport", 76),
	KHAZARD_TELEPORT("Khazard Teleport", 78),
	FISHING_GUILD_TELEPORT("Fishing Guild Teleport", 85),
	CATHERBY_TELEPORT("Catherby Teleport", 87),
	ICE_PLATEAU_TELEPORT("Ice Plateau Teleport", 89),
	TROLLHEIM_TELEPORT("Teleport to Trollheim", 92),

	TELE_GROUP_MOONCLAN("Tele Group Moonclan", 70),
	TELE_GROUP_WATERBIRTH("Tele Group Waterbirth", 73),
	TELE_GROUP_BARBARIAN("Tele Group Barbarian", 76),
	TELE_GROUP_KHAZARD("Tele Group Khazard", 79),
	TELE_GROUP_FISHING_GUILD("Tele Group Fishing Guild", 86),
	TELE_GROUP_CATHERBY("Tele Group Catherby", 88),
	TELE_GROUP_ICE_PLATEAU("Tele Group Ice Plateau", 90),
	TELE_GROUP_TROLLHEIM("Group Teleport to Trollheim", 93),

	BAKE_PIE("Bake Pie", 65),
	HUMIDIFY("Humidify", 68),
	HUNTER_KIT("Hunter Kit", 71),
	REPAIR_RUNE_POUCH("Repair Rune Pouch", 75),
	SUPERGLASS_MAKE("Superglass Make", 77),
	SPIRITUALISE_FOOD("Spiritualize Food", 80),
	STRING_JEWELLERY("String Jewellery", 80),
	PLANK_MAKE("Plank Make", 86),
	MAGIC_IMBUE("Magic Imbue", 82),
	MAKE_LEATHER("Make Leather", 83),

	CURE_PLANT("Cure Plant", 66),
	REMOTE_FARM("Remote Farm", 78),
	FERTILE_SOIL("Fertile Soil", 83),

	MONSTER_EXAMINE("Monster Examine", 66),
	NPC_CONTACT("NPC Contact", 67),
	STAT_SPY("Stat Spy", 75),
	TUNE_BANE_ORE("Tune Bane Ore", 87),

	CURE_OTHER("Cure Other", 68),
	CURE_ME("Cure Me", 71),
	CURE_GROUP("Cure Group", 74),
	STAT_RESTORE_POT_SHARE("Stat Restore Pot Share", 81),
	BOOST_POTION_SHARE("Boost Potion Share", 84),
	HEAL_OTHER("Heal Other", 92),
	HEAL_GROUP("Heal Group", 95),

	DREAM("Dream", 79),
	POLYPORE_STRIKE("Polypore Strike", 80),
	DISRUPTION_SHIELD("Disruption Shield", 90),
	VENGEANCE_OTHER("Vengeance Other", 93),
	VENGEANCE("Vengeance", 94),
	VENGEANCE_GROUP("Vengeance Group", 95),

	SPELLBOOK_SWAP("Spellbook Swap", 96),
	BORROWED_POWER("Borrowed Power", 99);

	private final String name;
	private final int requiredLevel;

	private LunarSpells(String name, int requiredLevel) {
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
		return Spellbook.LUNAR;
	}
}