package org.darkstorm.runescape.event.input;

import java.awt.event.KeyEvent;

public class KeyboardInputEvent extends InputEvent {
	public KeyboardInputEvent(KeyEvent inputEvent) {
		super(inputEvent);
	}

	@Override
	public KeyEvent getInputEvent() {
		return (KeyEvent) super.getInputEvent();
	}
}
