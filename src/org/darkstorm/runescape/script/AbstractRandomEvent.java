package org.darkstorm.runescape.script;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.event.script.*;

public abstract class AbstractRandomEvent implements RandomEvent, EventListener {
	protected final Bot bot;
	protected final RandomEventManager manager;
	protected final GameContext context;

	protected final Calculations calculations;
	protected final Game game;
	protected final Players players;
	protected final NPCs npcs;
	protected final GameObjects gameObjects;
	protected final GroundItems groundItems;
	protected final Skills skills;
	protected final Interfaces interfaces;
	protected final Menu menu;
	protected final Camera camera;
	protected final Inventory inventory;
	protected final Bank bank;
	protected final Walking walking;
	protected final Mouse mouse;
	protected final Keyboard keyboard;
	protected final Settings settings;
	protected final Filters filters;

	protected final Logger logger;

	private final AtomicBoolean active;
	private final AtomicBoolean paused;
	private final Lock stateLock;

	public AbstractRandomEvent(RandomEventManager manager) {
		this.manager = manager;
		bot = manager.getBot();

		context = bot.getGameContext();
		calculations = context.getCalculations();
		game = context.getGame();
		players = context.getPlayers();
		npcs = context.getNPCs();
		gameObjects = context.getGameObjects();
		groundItems = context.getGroundItems();
		skills = context.getSkills();
		interfaces = context.getInterfaces();
		menu = context.getMenu();
		camera = context.getCamera();
		inventory = context.getInventory();
		bank = context.getBank();
		walking = context.getWalking();
		mouse = context.getMouse();
		keyboard = context.getKeyboard();
		settings = context.getSettings();
		filters = context.getFilters();

		logger = Logger.getLogger(getManifest().name());

		active = new AtomicBoolean(false);
		paused = new AtomicBoolean(false);
		stateLock = new ReentrantLock(true);
	}

	@Override
	public final void start() {
		stateLock.lock();
		try {
			if(active.get())
				return;
			active.set(true);

			try {
				onStart();
			} catch(Throwable throwable) {
				reportError(throwable);
				active.set(false);
				return;
			}
			bot.getEventManager().sendEvent(new ScriptStartEvent(this));

			bot.getEventManager().registerListener(this);
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final void stop() {
		stateLock.lock();
		try {
			if(!active.get())
				return;
			active.set(false);

			bot.getEventManager().unregisterListener(this);

			try {
				onStop();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
			bot.getEventManager().sendEvent(new ScriptStopEvent(this));
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final void pause() {
		stateLock.lock();
		try {
			if(!active.get() || paused.get())
				return;
			paused.set(true);

			try {
				onPause();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
			bot.getEventManager().sendEvent(new ScriptPauseEvent(this));
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final void resume() {
		stateLock.lock();
		try {
			if(!active.get() || !paused.get())
				return;
			paused.set(false);

			try {
				onResume();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
			bot.getEventManager().sendEvent(new ScriptResumeEvent(this));
		} finally {
			stateLock.unlock();
		}
	}

	protected abstract void onStart();

	protected abstract void onStop();

	protected void onPause() {
	}

	protected void onResume() {
	}

	@Override
	public final boolean isPaused() {
		return paused.get();
	}

	@Override
	public final boolean isActive() {
		return active.get();
	}

	protected void reportError(Throwable throwable) {
		throwable.printStackTrace();
	}

	protected final void sleep(int time) {
		calculations.sleep(time);
	}

	protected final void sleep(int min, int max) {
		calculations.sleep(min, max);
	}

	protected final int random(int min, int max) {
		return calculations.random(min, max);
	}

	protected final double random(double min, double max) {
		return calculations.random(min, max);
	}

	@Override
	public GameContext getContext() {
		return context;
	}

	@Override
	public final ScriptManifest getManifest() {
		return getClass().getAnnotation(ScriptManifest.class);
	}

	@Override
	public final Bot getBot() {
		return bot;
	}

	@Override
	public final boolean isTopLevel() {
		return false;
	}
}
