package org.darkstorm.runescape.event;

public interface EventManager {
	public void registerListener(EventListener listener);

	public void unregisterListener(EventListener listener);

	public void clearListeners();

	public void sendEvent(Event event);

	public EventListener[] getListeners(Class<? extends Event> eventClass);
}
