package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface IgnoreTab extends Tab {
	public boolean isIgnoring(String name);

	public void addIgnore(String name);

	public void removeIgnore(String name);

	public InterfaceComponent getComponent(String name);
}
