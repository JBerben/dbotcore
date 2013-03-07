package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.*;

public interface Interfaces extends TypedUtility<Interface> {
	public boolean interfaceExists(Filter<Interface> filter);

	public boolean interfaceComponentExists(Filter<InterfaceComponent> filter);

	public Interface getInterface(Filter<Interface> filter);

	public InterfaceComponent getInterfaceComponent(
			Filter<InterfaceComponent> filter);

	public Interface[] getInterfaces();
}
