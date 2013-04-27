package org.darkstorm.runescape.api.pathfinding.astar;

import java.util.List;

import org.darkstorm.runescape.api.pathfinding.*;
import org.darkstorm.runescape.api.util.Tile;

public interface AStarHeuristic extends Heuristic {
	public Tile[] getSurrounding(AStarPathSearch search, Tile location);

	public double calculateGScore(AStarPathSearch search, PathNode node,
			boolean reverse);

	public double calculateFScore(AStarPathSearch search, PathNode node,
			boolean reverse);

	public PathNode findNext(List<PathNode> openSet);
}
