package org.darkstorm.runescape.script;

import org.darkstorm.runescape.GameType;

public @interface ScriptManifest {
	public String name();

	public String author();

	public String version();

	public GameType[] support();
}
