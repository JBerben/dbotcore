package org.darkstorm.runescape.util;

import org.darkstorm.runescape.GameType;

public class GameTypeSupportException extends RuntimeException {
	private final GameType type;

	public GameTypeSupportException(GameType type) {
		this.type = type;
	}

	public GameTypeSupportException(String message, GameType type) {
		super(message);
		this.type = type;
	}

	public GameTypeSupportException(Throwable cause, GameType type) {
		super(cause);
		this.type = type;
	}

	public GameTypeSupportException(String message, Throwable cause,
			GameType type) {
		super(message, cause);
		this.type = type;
	}

	public GameTypeSupportException(String message, Throwable cause,
			boolean enableSuppression, boolean writableStackTrace, GameType type) {
		super(message, cause, enableSuppression, writableStackTrace);
		this.type = type;
	}

	public GameType getType() {
		return type;
	}
}
