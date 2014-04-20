package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.util.Nameable;

public interface GroundItem extends Animable, Nameable {
	public Model getModel(GroundItemModelLayer layer);

	public static enum GroundItemModelLayer {
		TOP,
		MIDDLE,
		BOTTOM
	}
}
