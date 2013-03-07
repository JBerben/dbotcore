package org.darkstorm.runescape.api.util;

public enum PrayerSet {
	STANDARD("Prayers") {
		@Override
		public Prayer[] getPrayers() {
			return StandardPrayers.values();
		}
	},
	ANCIENT("Ancient Curses") {
		@Override
		public Prayer[] getPrayers() {
			return AncientPrayers.values();
		}
	};

	private final String name;

	private PrayerSet(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public abstract Prayer[] getPrayers();
}
