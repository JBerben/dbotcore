package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.input.MouseTargetable;
import org.darkstorm.runescape.api.util.*;

public interface Animable extends MouseTargetable, ScreenLocatable, Locatable,
		Identifiable, Wrapper {
	public void interact(String action);
}
