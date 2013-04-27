package org.darkstorm.runescape.api.pathfinding.astar;

import org.darkstorm.runescape.api.pathfinding.*;

public interface AStarPathSearch extends PathSearch {
	public Iterable<PathNode> getOpenSet();

	public Iterable<PathNode> getClosedSet();

	public Iterable<PathNode> getOpenSetReverse();

	public Iterable<PathNode> getClosedSetReverse();

	public Iterable<PathNode> getNodeWorld();

	public Iterable<PathNode> getNodeWorldReverse();
}
