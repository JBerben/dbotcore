package org.darkstorm.runescape.event.game;

import org.darkstorm.runescape.event.Event;


public class ServerMessageEvent extends Event {
	private final String message;

	public ServerMessageEvent(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}
