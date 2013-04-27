package org.darkstorm.runescape.api.pathfinding.astar;

import java.util.*;

import org.darkstorm.runescape.api.pathfinding.*;
import org.darkstorm.runescape.api.util.Tile;

public class AStarJPSPathSearch implements AStarPathSearch {
	private final AStarPathSearchProvider provider;
	private final AStarHeuristic heuristic;
	private final Tile start, end;

	private PathNode first, last, complete, completeReverse;

	private final List<PathNode> openSet, closedSet, openSetReverse,
			closedSetReverse;
	private final Map<Tile, PathNode> nodeWorld, nodeWorldReverse;

	public AStarJPSPathSearch(AStarPathSearchProvider provider,
			AStarHeuristic heuristic, Tile start, Tile end) {
		start = new Tile(start.getX(), start.getY(), start.getPlane());
		end = new Tile(end.getX(), end.getY(), end.getPlane());
		this.provider = provider;
		this.heuristic = heuristic;
		this.start = start;
		this.end = end;

		nodeWorld = new HashMap<Tile, PathNode>();
		nodeWorldReverse = new HashMap<Tile, PathNode>();

		first = new BasicPathNode(this, start);
		first.setGScore(heuristic.calculateGScore(this, first, false));
		first.setFScore(heuristic.calculateFScore(this, first, false));
		openSet = new ArrayList<PathNode>();
		closedSet = new ArrayList<PathNode>();
		nodeWorld.put(start, first);
		openSet.add(first);

		last = new BasicPathNode(this, end);
		last.setGScore(heuristic.calculateGScore(this, last, true));
		last.setFScore(heuristic.calculateFScore(this, last, true));
		openSetReverse = new ArrayList<PathNode>();
		closedSetReverse = new ArrayList<PathNode>();
		nodeWorld.put(end, last);
		openSetReverse.add(last);
	}

	@Override
	public void step() {
		if(isDone())
			return;

		PathNode current = heuristic.findNext(openSet);
		openSet.remove(current);

		if(complete == null && current.getLocation().equals(end)) {
			complete = reconstructPath(current);
			return;
		}
		calculate(current, false);

		if(completeReverse != null)
			return;

		PathNode currentReverse = heuristic.findNext(openSetReverse);
		openSetReverse.remove(currentReverse);

		if(completeReverse == null
				&& currentReverse.getLocation().equals(start)) {
			completeReverse = reconstructPath(currentReverse);
			if(complete == null)
				complete = reconstructPathReverse(completeReverse);
		} else if(completeReverse == null)
			calculate(currentReverse, true);
	}

	private void calculate(PathNode current, boolean reverse) {
		List<PathNode> openSet = (reverse ? openSetReverse : this.openSet);
		List<PathNode> closedSet = (reverse ? closedSetReverse : this.closedSet);

		closedSet.add(current);
		for(Tile adjacentLocation : heuristic.getSurrounding(this,
				current.getLocation())) {
			PathNode adjacent = getNode(current, adjacentLocation, reverse,
					true);
			adjacent = jump(current, adjacent, reverse, 0);
			if(adjacent == null)
				continue;

			if(closedSet.contains(adjacent) || !isWalkable(current, adjacent))
				continue;
			double cost = heuristic.calculateGScore(this, adjacent, reverse);

			boolean contained = openSet.contains(adjacent);
			if(!contained || cost < adjacent.getGScore()) {
				if(!contained)
					openSet.add(adjacent);
				adjacent.setPrevious(current);
				current.setNext(adjacent);
				adjacent.setGScore(cost);
				adjacent.setFScore(heuristic.calculateFScore(this, adjacent,
						reverse));
			}
		}
	}

	private PathNode jump(PathNode current, PathNode node, boolean reverse,
			int depth) {
		int x1 = current.getLocation().getX();
		int x2 = node.getLocation().getX();
		int y1 = current.getLocation().getY();
		int y2 = node.getLocation().getY();
		int offX = x2 - x1, offY = y2 - y1;

		if(!isWalkable(current, node))
			return null;
		if((reverse ? start : end).equals(node.getLocation()))
			return node;

		if(offX != 0 && offY != 0) {
			if((!isWalkable(node, getNode(node, x2 - offX, y2 + offY, reverse)) && isWalkable(
					node, getNode(node, x2 - offX, y2, reverse)))
					|| (!isWalkable(node,
							getNode(node, x2 + offX, y2 - offY, reverse)) && isWalkable(
							node, getNode(node, x2, y2 - offY, reverse))))
				return node;
		} else if(offX != 0) {
			if((!isWalkable(node, getNode(node, x2 + offX, y2 + 1, reverse)) && isWalkable(
					node, getNode(node, x2, y2 + 1, reverse)))
					|| (!isWalkable(node,
							getNode(node, x2 + offX, y2 - 1, reverse)) && isWalkable(
							node, getNode(node, x2, y2 - 1, reverse))))
				return node;
		} else if(offY != 0) {
			if((!isWalkable(node, getNode(node, x2 + 1, y2 + offY, reverse)) && isWalkable(
					node, getNode(node, x2 + 1, y2, reverse)))
					|| (!isWalkable(node,
							getNode(node, x2 - 1, y2 + offY, reverse)) && isWalkable(
							node, getNode(node, x2 - 1, y2, reverse)))) {
				return node;
			}
		}

		if(offX != 0 && offY != 0) {
			PathNode jumpX = jump(node, getNode(node, x2 + offX, y2, reverse),
					reverse, depth + 1);
			PathNode jumpY = jump(node, getNode(node, x2, y2 + offY, reverse),
					reverse, depth + 1);
			if(jumpX != null || jumpY != null)
				return node;
		}

		if(isWalkable(node, getNode(node, x2 + offX, y2, reverse))
				|| isWalkable(node, getNode(node, x2, y2 + offY, reverse)))
			return jump(node, getNode(node, x2 + offX, y2 + offY, reverse),
					reverse, depth + 1);
		return null;
	}

	private PathNode getNode(PathNode current, int x, int y, boolean reverse) {
		return getNode(current, new Tile(x, y), reverse, false);
	}

	private PathNode getNode(PathNode current, Tile tile, boolean reverse,
			boolean store) {
		PathNode node = (reverse ? nodeWorldReverse : nodeWorld).get(tile);
		if(node == null) {
			node = new BasicPathNode(this, tile);
			node.setPrevious(current);
			if(store)
				nodeWorld.put(tile, node);
		}
		return node;
	}

	private boolean isWalkable(PathNode current, PathNode node) {
		return heuristic.isWalkable(current, node)
				&& heuristic.isWalkable(node, current);
	}

	private PathNode reconstructPath(PathNode end) {
		PathNode current = end.getPrevious(), next = end;
		while(current != null) {
			current.setNext(next);
			next = current;
			current = next.getPrevious();
		}
		return next;
	}

	private PathNode reconstructPathReverse(PathNode start) {
		PathNode current = start, next = start.getNext();
		current.setNext(null);
		while(next != null) {
			PathNode after = next.getNext();
			current.setPrevious(next);
			next.setNext(current);
			current = next;
			next = after;
		}
		current.setPrevious(null);
		return current;
	}

	@Override
	public boolean isDone() {
		return complete != null || openSet.size() == 0
				|| (openSetReverse.size() == 0 && completeReverse == null);
	}

	@Override
	public Tile getStart() {
		return start;
	}

	@Override
	public Tile getEnd() {
		return end;
	}

	@Override
	public PathNode getPath() {
		return complete;
	}

	@Override
	public Iterable<PathNode> getOpenSet() {
		return openSet;
	}

	@Override
	public Iterable<PathNode> getClosedSet() {
		return closedSet;
	}

	@Override
	public Iterable<PathNode> getOpenSetReverse() {
		return openSetReverse;
	}

	@Override
	public Iterable<PathNode> getClosedSetReverse() {
		return closedSetReverse;
	}

	@Override
	public Iterable<PathNode> getNodeWorld() {
		return nodeWorld.values();
	}

	@Override
	public Iterable<PathNode> getNodeWorldReverse() {
		return nodeWorldReverse.values();
	}

	@Override
	public PathSearchProvider getSource() {
		return provider;
	}

}
