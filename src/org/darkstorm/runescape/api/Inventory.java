package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.Item;

public interface Inventory extends TypedUtility<Item> {
	public Item getItem(Filter<Item> filter);

	public Item[] getItems(Filter<Item> filter);

	public Item[] getItems();
}
