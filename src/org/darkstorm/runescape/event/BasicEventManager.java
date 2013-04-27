package org.darkstorm.runescape.event;

import java.lang.reflect.Method;
import java.util.*;

public final class BasicEventManager implements EventManager {
	private final List<EventSender> eventSenders;

	public BasicEventManager() {
		eventSenders = new ArrayList<EventSender>();
	}

	@Override
	public synchronized void registerListener(EventListener listener) {
		Class<? extends EventListener> listenerClass = listener.getClass();
		while(listenerClass != null) {
			for(Method method : listenerClass.getMethods()) {
				if(method.getAnnotation(EventHandler.class) == null)
					continue;
				if(!method.isAccessible())
					method.setAccessible(true);
				if(method.getParameterTypes().length != 1)
					throw new IllegalArgumentException("Method "
							+ method.toString() + " in class "
							+ listenerClass.getName()
							+ " has incorrect amount of parameters");
				Class<? extends Event> eventClass = method.getParameterTypes()[0]
						.asSubclass(Event.class);
				boolean senderExists = false;
				for(EventSender sender : eventSenders)
					if(eventClass.isAssignableFrom(sender
							.getListenerEventClass())) {
						sender.addHandler(listener, method);
						senderExists = true;
					}
				if(!senderExists) {
					EventSender sender = new EventSender(eventClass);
					eventSenders.add(sender);
					sender.addHandler(listener, method);
				}
			}
			if(!(EventListener.class.isAssignableFrom(listenerClass
					.getSuperclass())))
				break;
			listenerClass = listenerClass.getSuperclass().asSubclass(
					EventListener.class);
		}
	}

	@Override
	public synchronized void unregisterListener(EventListener listener) {
		for(EventSender sender : eventSenders)
			sender.unregisterListener(listener);
	}

	@Override
	public synchronized void clearListeners() {
		eventSenders.clear();
	}

	@Override
	public synchronized void sendEvent(Event event) {
		List<EventSender> sendTo = new ArrayList<EventSender>();
		for(EventSender sender : eventSenders) {
			Class<? extends Event> eventClass = sender.getListenerEventClass();
			if(eventClass.isInstance(event))
				sendTo.add(sender);
		}
		for(EventSender sender : sendTo)
			sender.sendEvent(event);
	}

	public synchronized EventListener[] getListeners(
			Class<? extends Event> eventClass) {
		EventListener[] emptyListeners = new EventListener[0];
		for(EventSender sender : eventSenders)
			if(eventClass.isAssignableFrom(sender.getListenerEventClass()))
				return sender.getListeners().toArray(emptyListeners);
		return emptyListeners;
	}
}
