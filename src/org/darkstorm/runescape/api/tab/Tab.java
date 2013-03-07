package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface Tab {
	public String getName();

	public InterfaceComponent getButtonComponent();

	public void open();

	public GameContext getContext();
}
