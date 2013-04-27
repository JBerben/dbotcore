package org.darkstorm.runescape.api.input;

import java.awt.event.*;

import org.darkstorm.runescape.api.GameContext;

public class KBot2Keyboard {
	private final GameContext context;

	public KBot2Keyboard(GameContext context) {
		this.context = context;
	}

	public void sendKeys(String text, boolean pressEnter) {
		char[] chs = text.toCharArray();
		for(char element : chs) {
			if((byte) element == -96) { // space fix for rs client
				element = KeyEvent.VK_SPACE;
			}
			sendKey(element, random(100, 220));
			sleep(random(100, 220));
		}
		if(pressEnter) {
			sendKey((char) KeyEvent.VK_ENTER, random(100, 200));
		}
	}

	private void sendKey(char ch, int delay) {
		boolean shift = false;
		int code = ch;
		if(ch >= 'a' && ch <= 'z') {
			code -= 32;
		} else if(ch >= 'A' && ch <= 'Z') {
			shift = true;
		}
		KeyEvent keyEvent;
		if(code == KeyEvent.VK_LEFT || code == KeyEvent.VK_RIGHT
				|| code == KeyEvent.VK_UP || code == KeyEvent.VK_DOWN) {
			keyEvent = new KeyEvent(context.getBot().getGame(),
					KeyEvent.KEY_PRESSED, System.currentTimeMillis() + delay,
					0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
			context.getBot().dispatchInputEvent(keyEvent);

			int delay2 = random(50, 120) + random(0, 100);
			keyEvent = new KeyEvent(context.getBot().getGame(),
					KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2,
					0, code, getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
			context.getBot().dispatchInputEvent(keyEvent);
		} else {
			if(!shift) {
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_PRESSED, System.currentTimeMillis()
								+ delay, 0, code, getKeyChar(ch),
						KeyEvent.KEY_LOCATION_STANDARD);
				context.getBot().dispatchInputEvent(keyEvent);

				// Event Typed
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0, 0,
						0, ch, 0);
				context.getBot().dispatchInputEvent(keyEvent);

				// Event Released
				int delay2 = random(50, 120) + random(0, 100);
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_RELEASED, System.currentTimeMillis()
								+ delay2, 0, code, getKeyChar(ch),
						KeyEvent.KEY_LOCATION_STANDARD);
				context.getBot().dispatchInputEvent(keyEvent);
			} else {
				// Press shift
				int s1 = random(25, 60) + random(0, 50);
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_PRESSED, System.currentTimeMillis() + s1,
						InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT,
						(char) KeyEvent.VK_UNDEFINED,
						KeyEvent.KEY_LOCATION_LEFT);
				context.getBot().dispatchInputEvent(keyEvent);

				// Press key
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_PRESSED, System.currentTimeMillis()
								+ delay, InputEvent.SHIFT_DOWN_MASK, code,
						getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				context.getBot().dispatchInputEvent(keyEvent);

				// Type key
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_TYPED, System.currentTimeMillis() + 0,
						InputEvent.SHIFT_DOWN_MASK, 0, ch, 0);
				context.getBot().dispatchInputEvent(keyEvent);

				// Release key
				int delay2 = random(50, 120) + random(0, 100);
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_RELEASED, System.currentTimeMillis()
								+ delay2, InputEvent.SHIFT_DOWN_MASK, code,
						getKeyChar(ch), KeyEvent.KEY_LOCATION_STANDARD);
				context.getBot().dispatchInputEvent(keyEvent);

				// Release shift
				int s2 = random(25, 60) + random(0, 50);
				keyEvent = new KeyEvent(context.getBot().getGame(),
						KeyEvent.KEY_RELEASED, System.currentTimeMillis() + s2,
						InputEvent.SHIFT_DOWN_MASK, KeyEvent.VK_SHIFT,
						(char) KeyEvent.VK_UNDEFINED,
						KeyEvent.KEY_LOCATION_LEFT);
				context.getBot().dispatchInputEvent(keyEvent);
			}
		}
	}

	private char getKeyChar(char c) {
		int i = (c);
		if(i >= 36 && i <= 40) {
			return KeyEvent.VK_UNDEFINED;
		} else {
			return c;
		}
	}

	public void holdKey(int keyCode, int millis) {
		KeyEvent keyEvent;
		// Press key
		keyEvent = new KeyEvent((context.getBot().getGame()),
				KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, keyCode,
				(char) keyCode);
		context.getBot().dispatchInputEvent(keyEvent);

		if(millis > 500) {
			keyEvent = new KeyEvent((context.getBot().getGame()),
					KeyEvent.KEY_PRESSED, System.currentTimeMillis() + 500, 0,
					keyCode, (char) keyCode);
			context.getBot().dispatchInputEvent(keyEvent);
			int ms2 = millis - 500;
			for(int i = 37; i < ms2; i += random(20, 40)) {
				keyEvent = new KeyEvent((context.getBot().getGame()),
						KeyEvent.KEY_PRESSED, System.currentTimeMillis() + (i)
								+ 500, 0, keyCode, (char) keyCode);
				context.getBot().dispatchInputEvent(keyEvent);
			}
		}
		int delay2 = millis + random(-30, 30);
		// release
		keyEvent = new KeyEvent((context.getBot().getGame()),
				KeyEvent.KEY_RELEASED, System.currentTimeMillis() + delay2, 0,
				keyCode, (char) keyCode);
		context.getBot().dispatchInputEvent(keyEvent);
	}

	public void pressKey(final char ch) {
		KeyEvent ke = new KeyEvent(context.getBot().getGame(),
				KeyEvent.KEY_PRESSED, System.currentTimeMillis(), 0, ch,
				getKeyChar(ch));
		context.getBot().dispatchInputEvent(ke);
	}

	public void releaseKey(final char ch) {
		KeyEvent ke;
		ke = new KeyEvent(context.getBot().getGame(), KeyEvent.KEY_RELEASED,
				System.currentTimeMillis(), InputEvent.ALT_DOWN_MASK, ch,
				getKeyChar(ch));
		context.getBot().dispatchInputEvent(ke);
	}

	private int random(int min, int max) {
		return context.getCalculations().random(min, max);
	}

	private void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch(InterruptedException exception) {
			throw new RuntimeException(exception);
		}
	}
}
