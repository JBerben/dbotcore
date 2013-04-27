package org.darkstorm.runescape.api.pathfinding.astar;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.pathfinding.PathSearchProvider;
import org.darkstorm.runescape.api.util.Tile;

public class AStarPathSearchProvider implements PathSearchProvider {
	private final GameContext context;

	private AStarHeuristic heuristic;

	public AStarPathSearchProvider(GameContext context) {
		this(context, new GlobalAStarHeuristic(context));
	}

	public AStarPathSearchProvider(GameContext context, AStarHeuristic heuristic) {
		this.context = context;
		this.heuristic = heuristic;
	}

	@Override
	public AStarPathSearch provideSearch(Tile start, Tile end) {
		return new SimpleAStarPathSearch(this, heuristic, start, end);
	}

	@Override
	public GameContext getContext() {
		return context;
	}

	@Override
	public AStarHeuristic getHeuristic() {
		return heuristic;
	}
}
