package org.darkstorm.runescape.api;

public interface GameContext {
	public Calculations getCalculations();

	public Players getPlayers();

	public NPCs getNPCs();

	public Mouse getMouse();

	public Keyboard getKeyboard();

	public Interfaces getInterfaces();

	public GroundItems getGroundItems();

	public GameObjects getGameObjects();

	public Skills getSkills();
}
