package org.darkstorm.runescape;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.InputEvent;
import java.util.logging.Logger;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.EventManager;
import org.darkstorm.runescape.script.*;

public interface Bot {
	public String getName();

	public InputState getInputState();

	public void setInputState(InputState state);

	public boolean canPlayScript();

	public Component getDisplay();

	public Applet getGame();

	public Canvas getCanvas();

	public Logger getLogger();

	public void dispatchInputEvent(InputEvent event);

	public EventManager getEventManager();

	public ScriptManager getScriptManager();

	public RandomEventManager getRandomEventManager();

	public GameContext getGameContext();

	public DarkBot getDarkBot();
}
