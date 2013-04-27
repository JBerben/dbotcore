package org.darkstorm.runescape.script;

@SuppressWarnings("serial")
public class ScriptLoadException extends Exception {

	public ScriptLoadException() {
	}

	public ScriptLoadException(String message) {
		super(message);
	}

	public ScriptLoadException(Throwable cause) {
		super(cause);
	}

	public ScriptLoadException(String message, Throwable cause) {
		super(message, cause);
	}

	public ScriptLoadException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
