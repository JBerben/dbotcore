package org.darkstorm.runescape.api.pathfinding;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.util.Tile;

public interface PathSearchProvider {
	public PathSearch provideSearch(Tile start, Tile end);

	public GameContext getContext();

	public Heuristic getHeuristic();
}
