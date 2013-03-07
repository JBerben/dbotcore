package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;

public interface Interfaces extends TypedUtility<Interface> {
	public boolean interfaceExists(Filter<Interface> filter);

	public boolean interfaceComponentExists(int id, int childId);

	public Interface getInterface(int id);

	public InterfaceComponent getComponent(int id, int childId);

	public Interface[] getInterfaces();
}
