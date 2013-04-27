package org.darkstorm.runescape.api.input;

import java.util.concurrent.*;

import org.darkstorm.runescape.api.GameContext;

public class BasicKeyboard implements Keyboard {
	private final GameContext context;
	private final KBot2Keyboard keyboard;
	private final ExecutorService service;

	private Future<Boolean> keyboardTask;

	public BasicKeyboard(GameContext context) {
		this.context = context;
		keyboard = new KBot2Keyboard(context);

		service = Executors.newSingleThreadExecutor();
	}

	@Override
	public GameContext getContext() {
		return context;
	}

	@Override
	public void typeKey(final char key) {
		typeMessage(Character.toString(key), false);
	}

	@Override
	public void typeMessage(final String message, final boolean pressEnter) {
		if(keyboardTask != null && !keyboardTask.isDone())
			keyboardTask.cancel(true);
		keyboardTask = service.submit(new Callable<Boolean>() {
			@Override
			public Boolean call() throws Exception {
				try {
					keyboard.sendKeys(message, true);
				} catch(Exception exception) {
					exception.printStackTrace();
					return Boolean.FALSE;
				}
				return Boolean.TRUE;
			}
		});
	}

	@Override
	public void pressKey(final char key) {
		if(keyboardTask != null && !keyboardTask.isDone())
			keyboardTask.cancel(true);
		keyboard.pressKey(key);
	}

	@Override
	public void releaseKey(final char key) {
		if(keyboardTask != null && !keyboardTask.isDone())
			keyboardTask.cancel(true);
		keyboard.releaseKey(key);
	}

	@Override
	public void holdKey(final char key, int duration) {
		if(keyboardTask != null && !keyboardTask.isDone())
			keyboardTask.cancel(true);
		keyboard.holdKey(key, duration);
	}

	@Override
	public boolean isActive() {
		return keyboardTask != null && !keyboardTask.isDone();
	}

	@Override
	public void stop() {
		if(keyboardTask != null && !keyboardTask.isDone())
			keyboardTask.cancel(true);
	}

	@Override
	public boolean await() {
		if(keyboardTask != null && !keyboardTask.isCancelled()) {
			try {
				return keyboardTask.get();
			} catch(Exception exception) {
				return false;
			}
		}
		return true;
	}

	@Override
	public boolean await(int timeout) {
		if(keyboardTask != null && !keyboardTask.isCancelled()) {
			try {
				return keyboardTask.get(timeout, TimeUnit.MILLISECONDS);
			} catch(Exception exception) {
				return false;
			}
		}
		return true;
	}

	@Override
	public GameContext getGameContext() {
		return context;
	}

}
