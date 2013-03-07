package org.darkstorm.runescape.api;

public interface Menu extends Utility {
	public int getActionIndex(String action);

	public String[] getActions();

	public boolean doAction(String action);

	public boolean doAction(int index);

	public boolean isOpen();

	public void close();

	public String getLastSelectedItemName();
}
