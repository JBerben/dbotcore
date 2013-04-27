package org.darkstorm.runescape.script;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public interface RandomEventManager extends TaskManager<RandomEvent> {
	public void enableRandoms();

	public void disableRandoms();

	public boolean allowRandoms();

	public GameContext getContext();

	public Bot getBot();
}
