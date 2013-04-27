package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.input.MouseTargetable;
import org.darkstorm.runescape.api.util.*;

public interface Animable extends Identifiable, MouseTargetable,
		ScreenLocatable, Locatable, Wrapper {
	public int getHeight();

	public Model getModel();
}
