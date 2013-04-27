package org.darkstorm.runescape;

import java.awt.Frame;

import org.darkstorm.runescape.event.EventManager;

public interface DarkBot {
	public Bot createBot(GameType type);

	public EventManager getEventManager();

	public Bot[] getBots();

	public Frame getFrame();
}
