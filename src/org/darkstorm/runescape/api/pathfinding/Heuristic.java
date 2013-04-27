package org.darkstorm.runescape.api.pathfinding;

public interface Heuristic {
	public boolean isWalkable(PathNode current, PathNode node);

	public boolean isObstruction(PathNode node);

	public boolean clearObstruction(PathNode node);
}
