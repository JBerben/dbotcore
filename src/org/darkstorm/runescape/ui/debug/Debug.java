package org.darkstorm.runescape.ui.debug;

import java.util.concurrent.atomic.AtomicBoolean;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.script.*;

public abstract class Debug implements Script, EventListener {
	protected final Bot bot;

	protected final AtomicBoolean active = new AtomicBoolean();

	public Debug(Bot bot) {
		this.bot = bot;
	}

	@Override
	public GameContext getContext() {
		return bot.getGameContext();
	}

	@Override
	public ScriptManifest getManifest() {
		return getClass().getAnnotation(ScriptManifest.class);
	}

	@Override
	public Bot getBot() {
		return bot;
	}

	@Override
	public void start() {
		if(active.get())
			throw new IllegalStateException();
		bot.getEventManager().registerListener(this);
		active.set(true);
	}

	@Override
	public void stop() {
		if(!active.get())
			throw new IllegalStateException();
		active.set(false);
		bot.getEventManager().unregisterListener(this);
	}

	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}

	@Override
	public boolean isPaused() {
		return false;
	}

	@Override
	public boolean isActive() {
		return active.get();
	}

	@Override
	public boolean isTopLevel() {
		return false;
	}

	public String getDisplayedText() {
		return null;
	}
}
