package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.util.*;

public interface Interface extends Identifiable, Wrapper {
	public InterfaceComponent[] getComponents();

	public InterfaceComponent getComponent(int id);

	public InterfaceComponent getComponent(Filter<InterfaceComponent> filter);

	public boolean isValid();
}
