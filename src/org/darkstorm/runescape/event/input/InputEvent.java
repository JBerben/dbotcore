package org.darkstorm.runescape.event.input;

import org.darkstorm.runescape.event.Event;

public abstract class InputEvent extends Event {
	private final java.awt.event.InputEvent inputEvent;

	public InputEvent(java.awt.event.InputEvent inputEvent) {
		this.inputEvent = inputEvent;
	}

	public java.awt.event.InputEvent getInputEvent() {
		return inputEvent;
	}
}
