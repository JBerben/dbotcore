package org.darkstorm.runescape.script;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public interface Script extends GameContext {
	public ScriptManifest getManifest();

	public Bot getBot();
}
