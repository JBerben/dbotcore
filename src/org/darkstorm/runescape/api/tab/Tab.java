package org.darkstorm.runescape.api.tab;

import java.awt.Rectangle;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

public interface Tab {
	public String getName();

	public InterfaceComponent getButtonComponent();

	public Rectangle getTabArea();

	public void open();

	@GameTypeSupport(GameType.CURRENT)
	public void close();

	public boolean isOpen();

	public GameContext getContext();
}
