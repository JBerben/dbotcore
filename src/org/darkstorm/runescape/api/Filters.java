package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.*;

public interface Filters {
	public Filter<NPC> npc(int... ids);

	public Filter<NPC> npc(String... names);

	public Filter<Player> player(String name);

	public Filter<GroundItem> ground(int... ids);

	public Filter<GameObject> object(int... ids);

	public Filter<Item> item(int... ids);

	public Filter<Item> item(String... names);

	public <T extends Identifiable> Filter<T> id(int... id);

	public <T extends Nameable> Filter<T> name(String... names);

	public <T extends Locatable> Filter<T> range(int range);

	public <T extends Locatable> Filter<T> range(Filter<T> filter, int range);

	public <T extends Locatable> Filter<T> range(Locatable origin, int range);

	public <T extends Locatable> Filter<T> range(Filter<T> filter,
			Locatable origin, int range);

	public <T extends Locatable> Filter<T> area(TileArea area);

	public <T extends Locatable> Filter<T> area(Filter<T> filter, TileArea area);

	public <T> Filter<T> all();

	public <T> Filter<T> none();

	public <T> Filter<T> all(TypedUtility<T> utility);

	public <T> Filter<T> none(TypedUtility<T> utility);

	public <T> Filter<T> inverse(Filter<T> filter);
}