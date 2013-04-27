package org.darkstorm.runescape.api.util;

import java.util.Comparator;

public class DistanceComparator implements Comparator<Locatable> {
	private final Tile origin;

	public DistanceComparator(Locatable origin) {
		this.origin = origin.getLocation();
	}

	@Override
	public int compare(Locatable o1, Locatable o2) {
		return Double.compare(origin.distanceTo(o1), origin.distanceTo(o2));
	}

}
