package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.input.*;

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

	public Menu getMenu();

	public Bank getBank();

	public Inventory getInventory();

	public Camera getCamera();

	public Walking getWalking();

	public Filters getFilters();
}
