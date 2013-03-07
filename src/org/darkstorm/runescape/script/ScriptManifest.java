package org.darkstorm.runescape.script;

import java.lang.annotation.*;

import org.darkstorm.runescape.GameType;

@Retention(RetentionPolicy.RUNTIME)
public @interface ScriptManifest {
	public String name();

	public String author();

	public String version();

	public GameType[] support();
}
