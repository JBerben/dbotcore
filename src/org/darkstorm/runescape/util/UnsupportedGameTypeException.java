package org.darkstorm.runescape.util;

import org.darkstorm.runescape.GameType;

@SuppressWarnings("serial")
public class UnsupportedGameTypeException extends RuntimeException {
	private final GameType type;

	public UnsupportedGameTypeException(GameType type) {
		this.type = type;
	}

	public UnsupportedGameTypeException(String message, GameType type) {
		super(message);
		this.type = type;
	}

	public UnsupportedGameTypeException(Throwable cause, GameType type) {
		super(cause);
		this.type = type;
	}

	public UnsupportedGameTypeException(String message, Throwable cause,
			GameType type) {
		super(message, cause);
		this.type = type;
	}

	public UnsupportedGameTypeException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace, GameType type) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.type = type;
	}

	public GameType getType() {
		return type;
	}
}
