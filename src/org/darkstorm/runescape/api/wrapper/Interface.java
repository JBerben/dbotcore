package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.util.Identifiable;

public interface Interface extends Identifiable, Wrapper {
	public InterfaceComponent[] getComponents();

	public InterfaceComponent getComponent(int id);

	public boolean isValid();
}
