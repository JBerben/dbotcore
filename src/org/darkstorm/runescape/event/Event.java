package org.darkstorm.runescape.event;

public abstract class Event {
	public String getName() {
		return getClass().getSimpleName();
	}
}
