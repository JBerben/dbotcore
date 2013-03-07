package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.NPC;

public interface NPCs extends TypedUtility<NPC> {
	public NPC getClosest(Filter<NPC> filter);

	public NPC[] getAll(Filter<NPC> filter);

	public NPC[] getAll();
}
