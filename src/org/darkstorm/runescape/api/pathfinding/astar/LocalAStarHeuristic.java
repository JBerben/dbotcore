package org.darkstorm.runescape.api.pathfinding.astar;

import java.util.List;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.pathfinding.PathNode;
import org.darkstorm.runescape.api.util.Tile;

public class LocalAStarHeuristic implements AStarHeuristic {
	private static final Tile[] surrounding = new Tile[] { new Tile(1.0, 0.0),
			new Tile(0.0, 1.0), new Tile(-1.0, 0.0), new Tile(0.0, -1.0),
			new Tile(1.0, 1.0), new Tile(1.0, -1.0), new Tile(-1.0, 1.0),
			new Tile(-1.0, -1.0), };

	private static final int WALL_NORTH_WEST = 0x1;
	private static final int WALL_NORTH = 0x2;
	private static final int WALL_NORTH_EAST = 0x4;
	private static final int WALL_EAST = 0x8;
	private static final int WALL_SOUTH_EAST = 0x10;
	private static final int WALL_SOUTH = 0x20;
	private static final int WALL_SOUTH_WEST = 0x40;
	private static final int WALL_WEST = 0x80;
	private static final int BLOCKED = 0x100;
	private static final int INVALID = 0x200000 | 0x40000;

	private final GameContext context;

	public LocalAStarHeuristic(GameContext context) {
		this.context = context;
	}

	@Override
	public Tile[] getSurrounding(AStarPathSearch search, Tile location) {
		Tile[] locations = new Tile[surrounding.length];
		for(int i = 0; i < locations.length; i++)
			locations[i] = location.offset(surrounding[i].getX(),
					surrounding[i].getY());
		return locations;
	}

	@Override
	public double calculateGScore(AStarPathSearch search, PathNode node,
			boolean reverse) {
		return node.isStart() ? 0 : node.getPrevious().getGScore()
				+ node.getLocation().distanceToSquared(
						node.getPrevious().getLocation());
	}

	@Override
	public double calculateFScore(AStarPathSearch search, PathNode node,
			boolean reverse) {
		return node.getLocation().distanceToSquared(
				reverse ? search.getStart() : search.getEnd());
	}

	@Override
	public boolean isWalkable(PathNode current, PathNode node) {
		Game game = context.getGame();
		int x = current.getLocation().getX(), y = current.getLocation().getY();
		int x2 = node.getLocation().getX(), y2 = node.getLocation().getY();
		int f_x = x - game.getRegionBaseX(), f_y = y - game.getRegionBaseY();
		int[][] flags = game.getTileCollisionData();
		int here = flags[f_x][f_y];
		int upper = flags.length - 1;
		if(x == x2 && y - 1 == y2)
			return(f_y > 0 && (here & WALL_SOUTH) == 0 && (flags[f_x][f_y - 1] & (BLOCKED | INVALID)) == 0);
		if(x - 1 == x2 && y == y2)
			return(f_x > 0 && (here & WALL_WEST) == 0 && (flags[f_x - 1][f_y] & (BLOCKED | INVALID)) == 0);
		if(x == x2 && y + 1 == y2)
			return(f_y < upper && (here & WALL_NORTH) == 0 && (flags[f_x][f_y + 1] & (BLOCKED | INVALID)) == 0);
		if(x + 1 == x2 && y == y2)
			return(f_x < upper && (here & WALL_EAST) == 0 && (flags[f_x + 1][f_y] & (BLOCKED | INVALID)) == 0);
		if(x - 1 == x2 && y - 1 == y2)
			return(f_x > 0
					&& f_y > 0
					&& (here & (WALL_SOUTH_WEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y - 1] & (BLOCKED | INVALID)) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | INVALID | WALL_WEST)) == 0 && (flags[f_x - 1][f_y] & (BLOCKED
					| INVALID | WALL_SOUTH)) == 0);
		if(x - 1 == x2 && y + 1 == y2)
			return(f_x > 0
					&& f_y < upper
					&& (here & (WALL_NORTH_WEST | WALL_NORTH | WALL_WEST)) == 0
					&& (flags[f_x - 1][f_y + 1] & (BLOCKED | INVALID)) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | INVALID | WALL_WEST)) == 0 && (flags[f_x - 1][f_y] & (BLOCKED
					| INVALID | WALL_NORTH)) == 0);
		if(x + 1 == x2 && y - 1 == y2)
			return(f_x < upper
					&& f_y > 0
					&& (here & (WALL_SOUTH_EAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y - 1] & (BLOCKED | INVALID)) == 0
					&& (flags[f_x][f_y - 1] & (BLOCKED | INVALID | WALL_EAST)) == 0 && (flags[f_x + 1][f_y] & (BLOCKED
					| INVALID | WALL_SOUTH)) == 0);
		if(x + 1 == x2 && y + 1 == y2)
			return(f_x < upper
					&& f_y < upper
					&& (here & (WALL_NORTH_EAST | WALL_NORTH | WALL_EAST)) == 0
					&& (flags[f_x + 1][f_y + 1] & (BLOCKED | INVALID)) == 0
					&& (flags[f_x][f_y + 1] & (BLOCKED | INVALID | WALL_EAST)) == 0 && (flags[f_x + 1][f_y] & (BLOCKED
					| INVALID | WALL_NORTH)) == 0);
		return false;
	}

	@Override
	public boolean isObstruction(PathNode node) {
		/*int id = world.getBlockIdAt(location);
		if(id == 8 || id == 9 || id == 65)
			return true;
		if(id == 106) {
			if(!isEmptyBlock(world.getBlockIdAt(location.getX(),
					location.getY(), location.getZ() + 1))
					|| !isEmptyBlock(world.getBlockIdAt(location.getX(),
							location.getY(), location.getZ() - 1))
					|| !isEmptyBlock(world.getBlockIdAt(location.getX() + 1,
							location.getY(), location.getZ()))
					|| !isEmptyBlock(world.getBlockIdAt(location.getX() - 1,
							location.getY(), location.getZ())))
				return true;
		}*/
		return false;
	}

	@Override
	public boolean clearObstruction(PathNode node) {
		return false;
	}

	@Override
	public PathNode findNext(List<PathNode> openSet) {
		PathNode best = null;
		double bestF = 0;
		for(PathNode node : openSet) {
			double f = node.getFScore() + node.getGScore();
			if(best == null || f < bestF) {
				best = node;
				bestF = f;
			}
		}
		return best;
	}
}
