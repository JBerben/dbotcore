package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.GroundItem;

public interface GroundItems extends TypedUtility<GroundItem> {
	public GroundItem getClosest(Filter<GroundItem> filter);

	public GroundItem[] getAll(Filter<GroundItem> filter);

	public GroundItem[] getAll();
}
