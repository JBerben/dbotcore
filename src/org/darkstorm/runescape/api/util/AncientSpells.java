package org.darkstorm.runescape.api.util;

public enum AncientSpells implements Spell {
	GALE_RUSH("Gale Rush", 50),
	ROCK_RUSH("Rock Rush", 52),
	BROODFIRE_RUSH("Bloodfire Rush", 56),
	ICE_RUSH("Ice Rush", 58),

	GALE_BURST("Gale Burst", 62),
	ROCK_BURST("Rock Burst", 64),
	BLOODRUSH_BURST("Bloodfire Burst", 68),
	ICE_BURST("Ice Burst", 70),

	GALE_BLITZ("Gale Blitz", 74),
	ROCK_BLITZ("Rock Blitz", 76),
	BLOODFIRE_BLITZ("Bloodfire Blitz", 80),
	ICE_BLITZ("Ice Blitz", 82),

	GALE_BARRAGE("Gale Barrage", 86),
	ROCK_BARRAGE("Rock Barrage", 88),
	BLOODFIRE_BARRAGE("Bloodfire Barrage", 92),
	ICE_BARRAGE("Ice Barrage", 92),

	POLYPORE_STRIKE("Polypore Strike", 80),

	HOME_TELEPORT("Home Teleport", 0),
	PADDEWWA_TELEPORT("Paddewwa Teleport", 54),
	SENNTISTEN_TELEPORT("Senntisten Teleport", 60),
	KHARYRLL_TELEPORT("Kharyrll Teleport", 66),
	LASSAR_TELEPORT("Lassar Teleport", 72),
	DAREEYAK_TELEPORT("Dareeyak Teleport", 78),
	CARRALLANGAR_TELEPORT("Carrallangar Teleport", 84),
	ANNAKARL_TELEPORT("Annakarl Teleport", 90),
	GHORROCK_TELEPORT("Ghorrock Teleport", 96);

	private final String name;
	private final int requiredLevel;

	private AncientSpells(String name, int requiredLevel) {
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
		return Spellbook.ANCIENT;
	}
}