package org.darkstorm.runescape.event;

public class CallbackEvent extends Event {
	private String callback;
	private Object[] arguments;
	private Object returnObject;
	private boolean cancelled;

	public CallbackEvent(String callback, Object[] arguments) {
		this.callback = callback;
		this.arguments = arguments;
	}

	public String getCallback() {
		return callback;
	}

	public Object[] getArguments() {
		return arguments;
	}

	public Object getReturnObject() {
		return returnObject;
	}

	public void setReturnObject(Object returnObject) {
		this.returnObject = returnObject;
	}

	public boolean isCancelled() {
		return cancelled;
	}

	public void setCancelled(boolean cancelled) {
		this.cancelled = cancelled;
	}
}
