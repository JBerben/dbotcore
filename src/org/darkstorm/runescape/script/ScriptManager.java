package org.darkstorm.runescape.script;

import java.io.*;
import java.net.URL;

import org.darkstorm.runescape.Bot;

public interface ScriptManager {
	public void loadScripts();

	public Script[] loadScripts(URL url) throws ScriptLoadException,
			IOException;

	public Script[] loadScripts(InputStream in) throws ScriptLoadException,
			IOException;

	public void loadScript(Script script) throws ScriptLoadException;

	public Script loadScript(Class<? extends Script> scriptClass)
			throws ScriptLoadException;

	public void unloadScripts();

	public void unloadScript(Script script);

	public Script getScript(String name);

	public Script[] getLoadedScripts();

	public Script[] getActiveScripts();

	public Bot getBot();
}
