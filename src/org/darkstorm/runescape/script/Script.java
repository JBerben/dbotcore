package org.darkstorm.runescape.script;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public interface Script extends GameContext, TaskManager {
	public ScriptManifest getManifest();

	public Bot getBot();

	public void start();

	public void stop();

	public void pause();

	public void resume();

	public boolean isPaused();

	public boolean isActive();
}
