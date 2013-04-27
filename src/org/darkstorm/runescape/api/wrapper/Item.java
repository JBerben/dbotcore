package org.darkstorm.runescape.api.wrapper;

import org.darkstorm.runescape.api.input.MouseTargetable;
import org.darkstorm.runescape.api.util.*;

public interface Item extends Nameable, Identifiable, MouseTargetable,
		ScreenLocatable, Wrapper {
	public int getStackSize();
}
