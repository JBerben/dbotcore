package org.darkstorm.runescape.api.wrapper;

public interface GroundItem extends Animable {
	public Model getModel(GroundItemModelLayer layer);

	public static enum GroundItemModelLayer {
		TOP,
		MIDDLE,
		BOTTOM
	}
}
