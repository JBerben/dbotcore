package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.wrapper.*;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface EquipmentTab extends Tab {
	public enum EquipmentSlot {
		HELMET,
		PLATEBODY,
		PLATELEGS,
		BOOTS,
		GLOVES,
		SHIELD,
		WEAPON,
		CAPE,
		AMMO,
		NECLACE,
		RING,
		@GameTypeSupport(GameType.CURRENT)
		AURA,
		@GameTypeSupport(GameType.CURRENT)
		POCKET
	}

	public Item getItem(EquipmentSlot slot);

	public boolean hasItem(EquipmentSlot slot);

	public void removeItem(EquipmentSlot slot);

	public InterfaceComponent getComponent(EquipmentSlot slot);
}
