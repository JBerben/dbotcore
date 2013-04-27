package org.darkstorm.runescape.script;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.concurrent.locks.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.api.GameContext;

public class RandomEventManagerImpl implements RandomEventManager, Runnable {
	protected final Bot bot;
	protected final GameContext context;

	private final ExecutorService service;
	private final AtomicReference<Future<?>> future;
	private final Map<RandomEvent, RandomEventContainer> randomEvents;
	private final AtomicBoolean active;
	private final Lock stateLock;

	public RandomEventManagerImpl(Bot bot) {
		this.bot = bot;
		context = bot.getGameContext();

		service = Executors.newCachedThreadPool();
		future = new AtomicReference<Future<?>>();
		randomEvents = new ConcurrentHashMap<RandomEvent, RandomEventContainer>();
		active = new AtomicBoolean(false);
		stateLock = new ReentrantLock(true);

		enableRandoms();
	}

	@Override
	public final synchronized void run() {
		if(!active.get())
			return;
		List<RandomEventContainer> randomEventContainers = new ArrayList<>();
		while(active.get()) {
			randomEventContainers.clear();
			synchronized(randomEvents) {
				randomEventContainers.addAll(randomEvents.values());
			}
			for(RandomEventContainer randomEvent : randomEventContainers) {
				try {
					if(!randomEvent.isActive()
							&& randomEvent.getRandomEvent().activate())
						randomEvent.setFuture(service.submit(randomEvent));
				} catch(Throwable throwable) {
					throwable.printStackTrace();
				}
			}
			context.getCalculations().sleep(50);
		}
	}

	@Override
	public final void register(RandomEvent randomEvent) {
		if(!active.get())
			return;
		if(randomEvent == null)
			throw new NullPointerException();
		synchronized(randomEvents) {
			if(randomEvents.containsKey(randomEvent))
				return;
			randomEvents
					.put(randomEvent, new RandomEventContainer(randomEvent));
		}
	}

	@Override
	public final void deregister(RandomEvent randomEvent) {
		if(!active.get())
			return;
		if(randomEvent == null)
			throw new NullPointerException();
		synchronized(randomEvents) {
			randomEvents.remove(randomEvent);
		}
	}

	@Override
	public final void stop(RandomEvent randomEvent) {
		if(!active.get())
			return;
		if(randomEvent == null)
			throw new NullPointerException();
		synchronized(randomEvents) {
			randomEvents.get(randomEvent).stop();
		}
	}

	@Override
	public final boolean isActive(RandomEvent randomEvent) {
		if(!active.get())
			return false;
		if(randomEvent == null)
			throw new NullPointerException();
		synchronized(randomEvents) {
			return randomEvents.get(randomEvent).isActive();
		}
	}

	@Override
	public final <T extends RandomEvent> T getTask(Class<T> taskClass) {
		synchronized(randomEvents) {
			for(RandomEvent randomEvent : randomEvents.keySet())
				if(taskClass.equals(randomEvent.getClass()))
					return taskClass.cast(randomEvent);
		}
		return null;
	}

	@Override
	public final RandomEvent[] getRegisteredTasks() {
		return randomEvents.keySet().toArray(
				new RandomEvent[randomEvents.size()]);
	}

	@Override
	public final RandomEvent[] getActiveTasks() {
		List<RandomEvent> activeRandomEvents = new ArrayList<RandomEvent>();
		synchronized(randomEvents) {
			for(RandomEventContainer randomEvent : randomEvents.values())
				if(randomEvent.isActive())
					activeRandomEvents.add(randomEvent.getRandomEvent());
		}
		return activeRandomEvents.toArray(new RandomEvent[activeRandomEvents
				.size()]);
	}

	@Override
	public void enableRandoms() {
		stateLock.lock();
		try {
			if(active.get())
				return;
			active.set(true);

			future.set(service.submit(this));
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public void disableRandoms() {
		stateLock.lock();
		try {
			if(!active.get())
				return;
			active.set(false);

			Future<?> future = this.future.get();
			if(future != null && !future.isDone())
				future.cancel(true);
			synchronized(randomEvents) {
				for(Task task : randomEvents.keySet())
					randomEvents.get(task).stop();
			}
		} finally {
			stateLock.unlock();
		}
	}

	@Override
	public boolean allowRandoms() {
		return active.get();
	}

	@Override
	public GameContext getContext() {
		return context;
	}

	@Override
	public Bot getBot() {
		return bot;
	}

	private final class RandomEventContainer implements Runnable {
		private final RandomEvent randomEvent;
		private AtomicReference<Future<?>> future;

		public RandomEventContainer(RandomEvent randomEvent) {
			this.randomEvent = randomEvent;
			future = new AtomicReference<Future<?>>();
		}

		@Override
		public void run() {
			try {
				randomEvent.start();
				try {
					while(isActive() && randomEvent.activate())
						randomEvent.run();
				} catch(Throwable throwable) {
					throwable.printStackTrace();
				}
				randomEvent.stop();
			} catch(Throwable throwable) {
				throwable.printStackTrace();
			}
		}

		public RandomEvent getRandomEvent() {
			return randomEvent;
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
