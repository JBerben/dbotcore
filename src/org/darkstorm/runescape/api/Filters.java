package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;

public interface Filters {
	public Filter<NPC> npc(int... ids);

	public Filter<NPC> npc(int range, int... ids);

	public Filter<NPC> npc(String... names);

	public Filter<NPC> npc(int range, String... names);

	public Filter<Player> player(String name);

	public Filter<GroundItem> ground(int... ids);

	public Filter<GroundItem> ground(int range, int... ids);

	public Filter<Item> item(int... ids);

	public Filter<Item> item(String... names);

	public Filter<Interface> inter(int id);

	public Filter<InterfaceComponent> component(int id, int childId);

	public Filter<InterfaceComponent> component(Interface inter, int childId);

	public <T> Filter<T> all();

	public <T> Filter<T> none();

	public <T> Filter<T> all(TypedUtility<T> utility);

	public <T> Filter<T> none(TypedUtility<T> utility);

	public <T> Filter<T> inverse(Filter<T> filter);
}
