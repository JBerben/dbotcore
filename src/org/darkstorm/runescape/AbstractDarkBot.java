package org.darkstorm.runescape;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.darkstorm.runescape.event.*;

public abstract class AbstractDarkBot implements DarkBot {
	private final List<Bot> bots;
	private final EventManager eventManager;

	public AbstractDarkBot() {
		eventManager = new BasicEventManager();
		bots = new CopyOnWriteArrayList<Bot>();
	}

	@Override
	public abstract Bot createBot(GameType type);

	protected void addBot(Bot bot) {
		synchronized(bots) {
			bots.add(bot);
		}
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public Bot[] getBots() {
		synchronized(bots) {
			return bots.toArray(new Bot[bots.size()]);
		}
	}
}
