package org.darkstorm.runescape.api;

import org.darkstorm.runescape.api.util.Filter;
import org.darkstorm.runescape.api.wrapper.Player;

public interface Players extends TypedUtility<Player> {
	public Player getSelf();

	public Player getClosest(Filter<Player> filter);

	public Player[] getAll(Filter<Player> filter);

	public Player[] getAll();
}
