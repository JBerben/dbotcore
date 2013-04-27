package org.darkstorm.runescape.script;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public interface Script {
	public ScriptManifest getManifest();

	public GameContext getContext();

	public Bot getBot();

	public void start();

	public void stop();

	public void pause();

	public void resume();

	public boolean isPaused();

	public boolean isActive();

	public boolean isTopLevel();
}
