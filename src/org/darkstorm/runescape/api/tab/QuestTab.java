package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface QuestTab extends Tab {
	public void scrollTo(String quest);

	public void openQuestWindow(String quest);

	public boolean hasStarted(String quest);

	public boolean hasCompleted(String quest);

	public InterfaceComponent getComponent(String quest);
}
