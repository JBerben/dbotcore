package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.input.MouseTargetable;

public interface Menu extends Utility {
	public int getActionIndex(String action);

	public String[] getActions();

	public boolean perform(String action);

	public boolean perform(int index);

	public boolean perform(MouseTargetable target, String action);

	public boolean isOpen();

	public void close();

	public String getLastSelectedItemName();
}
