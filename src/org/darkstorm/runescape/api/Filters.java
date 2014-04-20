package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.api.wrapper.GameObject.GameObjectType;

public interface Filters extends Utility {
	public Filter<NPC> npc(int... ids);

	public Filter<NPC> npc(String... names);

	public Filter<Player> player(String name);

	public Filter<GroundItem> ground(int... ids);

	public Filter<GroundItem> ground(String... names);

	public Filter<GameObject> object(int... ids);

	public Filter<GameObject> object(GameObjectType... types);

	public Filter<Item> item(int... ids);

	public Filter<Item> item(String... names);

	public Filter<Interface> inter(int... ids);

	public Filter<Interface> inter(String... text);

	public Filter<InterfaceComponent> component(int id, int... childIds);

	public Filter<InterfaceComponent> component(String... text);

	public Filter<InterfaceComponent> component(Filter<Interface> filter, int... childIds);

	public Filter<InterfaceComponent> component(Filter<Interface> filter, String... text);

	public <T extends Identifiable> Filter<T> id(int... id);

	public <T extends Nameable> Filter<T> name(String... names);

	public <T extends Locatable> Filter<T> range(int range);

	public <T extends Locatable> Filter<T> range(Locatable origin, int range);

	public <T extends Locatable> Filter<T> area(TileArea area);

	public <T> Filter<T> all();

	public <T> Filter<T> none();

	public <T> Filter<T> all(TypedUtility<T> utility);

	public <T> Filter<T> none(TypedUtility<T> utility);

	public <T> Filter<T> inverse(Filter<T> filter);

	public <T> Filter<T> chain(Filter<T> filter1, Filter<T> filter2);

	public <T> Filter<T> chain(@SuppressWarnings("unchecked") Filter<T>... filters);
}