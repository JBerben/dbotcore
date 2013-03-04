package org.darkstorm.runescape.script;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;

public abstract class AbstractScript implements Script {
	protected final Bot bot;

	protected final Calculations calculations;
	protected final Players players;
	protected final NPCs npcs;
	protected final GameObjects gameObjects;
	protected final GroundItems groundItems;
	protected final Skills skills;
	protected final Interfaces interfaces;
	protected final Mouse mouse;
	protected final Keyboard keyboard;

	public AbstractScript(Bot bot) {
		this.bot = bot;

		GameContext context = bot.getGameContext();
		calculations = context.getCalculations();
		players = context.getPlayers();
		npcs = context.getNPCs();
		gameObjects = context.getGameObjects();
		groundItems = context.getGroundItems();
		skills = context.getSkills();
		interfaces = context.getInterfaces();
		mouse = context.getMouse();
		keyboard = context.getKeyboard();
	}

	@Override
	public final Calculations getCalculations() {
		return calculations;
	}

	@Override
	public final Players getPlayers() {
		return players;
	}

	@Override
	public final NPCs getNPCs() {
		return npcs;
	}

	@Override
	public final Mouse getMouse() {
		return mouse;
	}

	@Override
	public final Keyboard getKeyboard() {
		return keyboard;
	}

	@Override
	public final Interfaces getInterfaces() {
		return interfaces;
	}

	@Override
	public final GroundItems getGroundItems() {
		return groundItems;
	}

	@Override
	public final GameObjects getGameObjects() {
		return gameObjects;
	}

	@Override
	public final Skills getSkills() {
		return skills;
	}

	@Override
	public final ScriptManifest getManifest() {
		return getClass().getAnnotation(ScriptManifest.class);
	}

	@Override
	public Bot getBot() {
		return bot;
	}

}
