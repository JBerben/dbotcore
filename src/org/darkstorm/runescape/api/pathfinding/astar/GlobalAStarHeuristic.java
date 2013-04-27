package org.darkstorm.runescape.api.pathfinding.astar;

import java.util.*;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.pathfinding.PathNode;
import org.darkstorm.runescape.api.util.Tile;

public class GlobalAStarHeuristic implements AStarHeuristic {
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
	private static final int CHUNK_SIZE = 8;

	private final GameContext context;

	// public final IntHashMap<Region> regions;
	public final Map<Integer, Region> regions;

	public GlobalAStarHeuristic(GameContext context) {
		this.context = context;
		// regions = new IntHashMap<>(0xFFFFF);
		regions = new HashMap<>(0xFFF);
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
		int x = current.getLocation().getX(), y = current.getLocation().getY();
		Game game = context.getGame();
		int rx = game.getRegionBaseX(), ry = game.getRegionBaseY();
		if(game.getCurrentFloor() == 0) {
			int[][] flagData = game.getTileCollisionData();
			int[][] newArray = flagData.clone();
			for(int i = 0; i < flagData.length; i++)
				newArray[i] = flagData[i].clone();
			for(int i = 1; i < newArray.length / CHUNK_SIZE - 1; i++) {
				int[] ydata = newArray[i];
				for(int j = 1; j < ydata.length / CHUNK_SIZE - 1; j++) {
					int[][] subdata = new int[CHUNK_SIZE][CHUNK_SIZE];
					for(int sx = 0; sx < CHUNK_SIZE; sx++)
						for(int sy = 0; sy < CHUNK_SIZE; sy++)
							subdata[sx][sy] = newArray[i * CHUNK_SIZE + sx][j
									* CHUNK_SIZE + sy];
					Region region = getRegion(rx + i * CHUNK_SIZE, ry + j
							* CHUNK_SIZE);
					if(region != null)
						regions.remove(calcId(region.x, region.y));
					addRegion(new Region(rx + i * CHUNK_SIZE, ry + j
							* CHUNK_SIZE, subdata));
				}
			}
		}
		int x2 = node.getLocation().getX(), y2 = node.getLocation().getY();
		int here = flag(x, y);
		int upper = Integer.MAX_VALUE;
		if(x == x2 && y - 1 == y2)
			return(y > 0 && (here & WALL_SOUTH) == 0 && (flag(x, y - 1) & (BLOCKED | INVALID)) == 0);
		if(x - 1 == x2 && y == y2)
			return(x > 0 && (here & WALL_WEST) == 0 && (flag(x - 1, y) & (BLOCKED | INVALID)) == 0);
		if(x == x2 && y + 1 == y2)
			return(y < upper && (here & WALL_NORTH) == 0 && (flag(x, y + 1) & (BLOCKED | INVALID)) == 0);
		if(x + 1 == x2 && y == y2)
			return(x < upper && (here & WALL_EAST) == 0 && (flag(x + 1, y) & (BLOCKED | INVALID)) == 0);
		if(x - 1 == x2 && y - 1 == y2)
			return(x > 0 && y > 0
					&& (here & (WALL_SOUTH_WEST | WALL_SOUTH | WALL_WEST)) == 0
					&& (flag(x - 1, y - 1) & (BLOCKED | INVALID)) == 0
					&& (flag(x, y - 1) & (BLOCKED | INVALID | WALL_WEST)) == 0 && (flag(
					x - 1, y) & (BLOCKED | INVALID | WALL_SOUTH)) == 0);
		if(x - 1 == x2 && y + 1 == y2)
			return(x > 0 && y < upper
					&& (here & (WALL_NORTH_WEST | WALL_NORTH | WALL_WEST)) == 0
					&& (flag(x - 1, y + 1) & (BLOCKED | INVALID)) == 0
					&& (flag(x, y + 1) & (BLOCKED | INVALID | WALL_WEST)) == 0 && (flag(
					x - 1, y) & (BLOCKED | INVALID | WALL_NORTH)) == 0);
		if(x + 1 == x2 && y - 1 == y2)
			return(x < upper && y > 0
					&& (here & (WALL_SOUTH_EAST | WALL_SOUTH | WALL_EAST)) == 0
					&& (flag(x + 1, y - 1) & (BLOCKED | INVALID)) == 0
					&& (flag(x, y - 1) & (BLOCKED | INVALID | WALL_EAST)) == 0 && (flag(
					x + 1, y) & (BLOCKED | INVALID | WALL_SOUTH)) == 0);
		if(x + 1 == x2 && y + 1 == y2)
			return(x < upper && y < upper
					&& (here & (WALL_NORTH_EAST | WALL_NORTH | WALL_EAST)) == 0
					&& (flag(x + 1, y + 1) & (BLOCKED | INVALID)) == 0
					&& (flag(x, y + 1) & (BLOCKED | INVALID | WALL_EAST)) == 0 && (flag(
					x + 1, y) & (BLOCKED | INVALID | WALL_NORTH)) == 0);
		return false;
	}

	private int flag(int x, int y) {
		Region region = getRegion(x, y);
		if(region == null)
			return -1;
		int[][] flags = region.flags;
		return flags[x % CHUNK_SIZE][y % CHUNK_SIZE];
	}

	private int calcId(int x, int y) {
		return (((x / CHUNK_SIZE) & 0xFFF) << 12) | ((y / CHUNK_SIZE) & 0xFFF);
	}

	private Region getRegion(int x, int y) {
		x -= x % CHUNK_SIZE;
		y -= y % CHUNK_SIZE;
		return regions.get(calcId(x, y));
	}

	public void addRegion(Region region) {
		regions.put(calcId(region.x, region.y), region);
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
			double f = node.getFScore();
			if(best == null || f < bestF) {
				best = node;
				bestF = f;
			}
		}
		return best;
	}

	public static class Region {
		public final int x, y;
		public final int[][] flags;

		public Region(int x, int y, int[][] flags) {
			this.x = x;
			this.y = y;
			this.flags = flags;
		}
	}
}
