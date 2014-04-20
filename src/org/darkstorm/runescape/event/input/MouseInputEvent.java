package org.darkstorm.runescape.event.input;

import java.awt.event.MouseEvent;

public class MouseInputEvent extends InputEvent {
	public MouseInputEvent(MouseEvent inputEvent) {
		super(inputEvent);
	}

	@Override
	public MouseEvent getInputEvent() {
		return (MouseEvent) super.getInputEvent();
	}
}
