package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.Item;

public interface Inventory extends TypedUtility<Item> {
	public boolean contains(Filter<Item> filter);

	public int getCount(Filter<Item> filter);

	public Item getItem(Filter<Item> filter);

	public Item[] getItems(Filter<Item> filter);

	public Item[] getItems();

	public boolean isFull();
}
