package org.darkstorm.runescape.script;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;
import java.util.logging.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.event.script.*;

public abstract class AbstractScript implements Script, Runnable,
		TaskManager<Task>, EventListener {
	protected final Bot bot;
	protected final ScriptManager manager;
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

	private final ExecutorService service;
	private final AtomicReference<Future<?>> future;
	private final Map<Task, TaskContainer> tasks;
	private final AtomicBoolean active;
	private final AtomicBoolean paused;
	private final Lock stateLock;

	private int refreshDelay = 50;

	public AbstractScript(ScriptManager manager) {
		if(manager == null)
			throw new NullPointerException();
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
		for(Handler handler : logger.getHandlers())
			logger.removeHandler(handler);
		for(Handler handler : bot.getLogger().getHandlers())
			logger.addHandler(handler);

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
				return;
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
			return;
		// Future<?> future = this.future.get();
		// if(future != null && !future.isDone())
		// throw new IllegalStateException();
		activeLoop: while(active.get() && !paused.get()) {
			List<TaskContainer> taskContainers;
			synchronized(tasks) {
				taskContainers = new ArrayList<>(tasks.values());
			}
			for(TaskContainer task : taskContainers) {
				try {
					if(!task.isActive() && task.getTask().activate())
						task.setFuture(service.submit(task));
				} catch(Throwable throwable) {
					if(throwable instanceof ThreadDeath)
						continue activeLoop;
					reportError(throwable);
				}
			}
			sleep(refreshDelay);
		}
	}

	@Override
	public final void register(Task task) {
		if(!active.get())
			return;
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
			return;
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			tasks.remove(task);
		}
	}

	@Override
	public final void stop(Task task) {
		if(!active.get())
			return;
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			tasks.get(task).stop();
		}
	}

	@Override
	public final boolean isActive(Task task) {
		if(!active.get())
			return false;
		if(task == null)
			throw new NullPointerException();
		synchronized(tasks) {
			return tasks.get(task).isActive();
		}
	}

	@Override
	public final <T extends Task> T getTask(Class<T> taskClass) {
		synchronized(tasks) {
			for(Task task : tasks.keySet())
				if(task instanceof BranchTask)
					return getTask((BranchTask) task, taskClass);
				else if(taskClass.equals(task.getClass()))
					return taskClass.cast(task);
		}
		return null;
	}

	private <T extends Task> T getTask(BranchTask task, Class<T> taskClass) {
		for(Task subtask : task.getTasks())
			if(subtask instanceof BranchTask)
				return getTask((BranchTask) subtask, taskClass);
			else if(taskClass.equals(task.getClass()))
				return taskClass.cast(task);
		return null;
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

	protected void reportError(Throwable throwable) {
		if(throwable instanceof ThreadDeath)
			return;
		throwable.printStackTrace();
	}

	protected final int getRefreshDelay() {
		return refreshDelay;
	}

	protected final void setRefreshDelay(int refreshDelay) {
		this.refreshDelay = refreshDelay;
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
		return true;
	}

	public Logger getLogger() {
		return logger;
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
				while(isActive() && task.activate()) {
					try {
						task.run();
					} catch(Throwable throwable) {
						if(throwable instanceof ThreadDeath)
							continue;
						reportError(throwable);
					}
				}
			} catch(Throwable throwable) {
				if(throwable instanceof ThreadDeath)
					return;
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
