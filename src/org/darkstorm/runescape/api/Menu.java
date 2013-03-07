package org.darkstorm.runescape.api;

public interface Menu extends Utility {
	public int getActionIndex(String action);

	public String[] getActions();

	public boolean perform(String action);

	public boolean perform(int index);

	public boolean isOpen();

	public void close();

	public String getLastSelectedItemName();
}
