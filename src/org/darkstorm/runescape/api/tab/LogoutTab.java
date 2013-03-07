package org.darkstorm.runescape.api.tab;

import org.darkstorm.runescape.GameType;
import org.darkstorm.runescape.api.wrapper.InterfaceComponent;
import org.darkstorm.runescape.util.GameTypeSupport;

@GameTypeSupport(GameType.OLDSCHOOL)
public interface LogoutTab extends Tab {
	public void logout();

	public InterfaceComponent getLogoutButtonComponent();
}
