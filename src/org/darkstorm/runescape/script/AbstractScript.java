package org.darkstorm.runescape.script;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.logging.Logger;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.event.EventListener;

public abstract class AbstractScript implements Script, Runnable, EventListener {
	protected final Bot bot;

	protected final Calculations calculations;
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
	protected final Filters filters;

	protected final Logger logger;

	private final ExecutorService service;
	private final AtomicReference<Future<?>> future;
	private final Map<Task, TaskContainer> tasks;
	private final AtomicBoolean active;
	private final AtomicBoolean paused;
	private final Lock stateLock;

	public AbstractScript(Bot bot) {
		this.bot = bot;

		GameContext context = bot.getGameContext();
		calculations = context.getCalculations();
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
		filters = context.getFilters();

		logger = Logger.getLogger(getManifest().name());

		service = Executors.newCachedThreadPool();
		future = new AtomicReference<Future<?>>();
		tasks = new ConcurrentHashMap<Task, TaskContainer>();
		active = new AtomicBoolean(false);
		paused = new AtomicBoolean(false);
		stateLock = new ReentrantLock(true);
	}

	@Override
	public final void start() {
		stateLock.lock();
		try {
			if(active.get())
				throw new IllegalStateException();
			active.set(true);

			try {
				onStart();
			} catch(Throwable throwable) {
				reportError(throwable);
				active.set(false);
				return;
			}
			future.set(service.submit(this));

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
				throw new IllegalStateException();
			active.set(false);

			bot.getEventManager().unregisterListener(this);

			Future<?> future = this.future.get();
			if(future != null && !future.isDone())
				future.cancel(true);
			synchronized(tasks) {
				for(Task task : tasks.keySet())
					tasks.get(task).stop();
				tasks.clear();
			}
			try {
				onStop();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final void pause() {
		stateLock.lock();
		try {
			if(!active.get() || paused.get())
				throw new IllegalStateException();
			paused.set(true);

			Future<?> future = this.future.get();
			if(future != null && !future.isDone())
				future.cancel(true);
			synchronized(tasks) {
				for(Task task : tasks.keySet())
					tasks.get(task).stop();
			}
			try {
				onPause();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public final void resume() {
		stateLock.lock();
		try {
			if(!active.get() || !paused.get())
				throw new IllegalStateException();
			paused.set(false);

			try {
				onResume();
			} catch(Throwable throwable) {
				reportError(throwable);
			}
			future.set(service.submit(this));
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

	@Override
	public final void run() {
		if(!active.get())
			throw new IllegalStateException();
		Future<?> future = this.future.get();
		if(future != null && !future.isDone())
			throw new IllegalStateException();
		while(active.get() && !paused.get()) {
			synchronized(tasks) {
				for(TaskContainer task : tasks.values()) {
					try {
						if(!task.isActive() && task.getTask().activate())
							task.setFuture(service.submit(task));
					} catch(Throwable throwable) {
						reportError(throwable);
					}
				}
			}
			sleep(25);
		}
	}

	@Override
	public final void register(Task task) {
		if(!active.get())
			throw new IllegalStateException();
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			if(tasks.containsKey(task))
				return;
			tasks.put(task, new TaskContainer(task));
		}
	}

	@Override
	public final void deregister(Task task) {
		if(!active.get())
			throw new IllegalStateException();
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			tasks.remove(task);
		}
	}

	@Override
	public final void stop(Task task) {
		if(!active.get())
			throw new IllegalStateException();
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			tasks.get(task).stop();
		}
	}

	@Override
	public final boolean isActive(Task task) {
		if(!active.get())
			throw new IllegalStateException();
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			return tasks.get(task).isActive();
		}
	}

	@Override
	public final Task[] getRegisteredTasks() {
		return tasks.keySet().toArray(new Task[tasks.size()]);
	}

	@Override
	public final Task[] getActiveTasks() {
		List<Task> activeTasks = new ArrayList<Task>();
		synchronized(tasks) {
			for(TaskContainer task : tasks.values())
				if(task.isActive())
					activeTasks.add(task.getTask());
		}
		return activeTasks.toArray(new Task[activeTasks.size()]);
	}

	private final void reportError(Throwable throwable) {
		throwable.printStackTrace();
	}

	protected final void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException exception) {}
	}

	protected final void sleep(int min, int max) {
		sleep(random(min, max));
	}

	protected final int random(int min, int max) {
		return calculations.random(min, max);
	}

	protected final double random(double min, double max) {
		return calculations.random(min, max);
	}

	@Override
	public final Calculations getCalculations() {
		return calculations;
	}

	@Override
	public final Players getPlayers() {
		return players;
	}

	@Override
	public final NPCs getNPCs() {
		return npcs;
	}

	@Override
	public final Mouse getMouse() {
		return mouse;
	}

	@Override
	public final Keyboard getKeyboard() {
		return keyboard;
	}

	@Override
	public final Interfaces getInterfaces() {
		return interfaces;
	}

	@Override
	public final GroundItems getGroundItems() {
		return groundItems;
	}

	@Override
	public final GameObjects getGameObjects() {
		return gameObjects;
	}

	@Override
	public final Skills getSkills() {
		return skills;
	}

	@Override
	public Menu getMenu() {
		return menu;
	}

	@Override
	public Camera getCamera() {
		return camera;
	}

	@Override
	public Bank getBank() {
		return bank;
	}

	@Override
	public Inventory getInventory() {
		return inventory;
	}

	@Override
	public Walking getWalking() {
		return walking;
	}

	@Override
	public Filters getFilters() {
		return filters;
	}

	@Override
	public final ScriptManifest getManifest() {
		return getClass().getAnnotation(ScriptManifest.class);
	}

	@Override
	public final Bot getBot() {
		return bot;
	}

	private final class TaskContainer implements Runnable {
		private final Task task;
		private AtomicReference<Future<?>> future;

		public TaskContainer(Task task) {
			this.task = task;
			future = new AtomicReference<Future<?>>();
		}

		@Override
		public void run() {
			try {
				while(task.activate()) {
					try {
						task.run();
					} catch(Throwable throwable) {
						reportError(throwable);
					}
				}
			} catch(Throwable throwable) {
				reportError(throwable);
			}
		}

		public Task getTask() {
			return task;
		}

		public Future<?> getFuture() {
			return future.get();
		}

		public void setFuture(Future<?> future) {
			this.future.set(future);
		}

		public boolean isActive() {
			Future<?> future = getFuture();
			if(future == null)
				return false;
			return !future.isDone();
		}

		public void stop() {
			Future<?> future = getFuture();
			if(future == null || future.isDone())
				return;
			future.cancel(true);
		}
	}
}
