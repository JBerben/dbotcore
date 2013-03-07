package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface EmoteTab extends Tab {
	public String getEmote(int row, int column);

	public void performEmote(String name);

	public boolean isUnlocked(String name);

	public InterfaceComponent getComponent(String name);
}
