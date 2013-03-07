package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.api.util.Skill;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;

public interface SkillTab extends Tab {
	public void openSkillWindow(Skill skill);

	public int getTotalLevel();

	public InterfaceComponent getComponent(Skill skill);
}
