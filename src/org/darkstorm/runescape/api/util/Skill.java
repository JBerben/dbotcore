package org.darkstorm.runescape.api.util;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.util.GameTypeSupport;

public enum Skill {
	ATTACK,
	DEFENSE,
	STRENGTH,
	CONSTITUTION,
	RANGED,
	PRAYER,
	MAGIC,
	COOKING,
	WOODCUTTING,
	FLETCHING,
	FISHING,
	FIREMAKING,
	CRAFTING,
	SMITHING,
	MINING,
	HERBLORE,
	AGILITY,
	THIEVING,
	SLAYER,
	FARMING,
	RUNECRAFTING,
	HUNTER,
	CONSTRUCTION,
	@GameTypeSupport(GameType.CURRENT)
	SUMMONING,
	@GameTypeSupport(GameType.CURRENT)
	DUNGEONEERING
}