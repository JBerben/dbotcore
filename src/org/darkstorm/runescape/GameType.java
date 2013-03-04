package org.darkstorm.runescape;

public enum GameType {
	CURRENT("RuneScape"),
	OLDSCHOOL("RuneScape 2007");

	private final String name;

	private GameType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return getName();
	}
}
