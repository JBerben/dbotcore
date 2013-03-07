package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.Item;

public interface Bank extends TypedUtility<Item> {
	public enum DepositShortcut {
		INVENTORY,
		EQUIPMENT,
		BEAST_OF_BURDEN
	}

	public Item getItem(Filter<Item> filter);

	public Item[] getItems(Filter<Item> filter);

	public Item[] getItems();

	public void withdrawAll(Item item);

	public void withdrawAllButOne(Item item);

	public void withdrawX(Item item, int amount);

	public void scrollTo(Item item);

	public boolean isOpen();

	public void close();

	public void openNearest();

	public void walkToNearest();

	public void deposit(Filter<Item> filter);

	public void depositShortcut(DepositShortcut shortcut);

	public boolean isNoted();

	public void setNoted(boolean noted);
}
