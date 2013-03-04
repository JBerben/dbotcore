package org.darkstorm.runescape;

import java.util.logging.Logger;

import java.applet.Applet;
import java.awt.Component;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.EventManager;

public interface Bot {
	public String getName();

	public Component getDisplay();

	public Applet getGame();

	public Logger getLogger();

	public EventManager getEventManager();

	public GameContext getGameContext();

	public DarkBotRS getDarkBot();
}
