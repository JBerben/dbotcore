package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.Inventory;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface InventoryTab extends Tab {
	public Inventory getInventory();

	public InterfaceComponent getItemComponent(int row, int column);
}
