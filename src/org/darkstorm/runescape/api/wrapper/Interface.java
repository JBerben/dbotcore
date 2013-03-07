package org.darkstorm.runescape.api.wrapper;

public interface Interface extends Wrapper {
	public InterfaceComponent[] getComponents();

	public InterfaceComponent getComponent(int id);

	public int getIndex();

	public boolean isValid();
}
