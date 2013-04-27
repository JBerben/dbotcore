package org.darkstorm.runescape.api.pathfinding.astar;

import java.util.*;

import org.darkstorm.runescape.api.pathfinding.*;
import org.darkstorm.runescape.api.util.Tile;

public class SimpleAStarPathSearch implements AStarPathSearch {
	private final AStarPathSearchProvider provider;
	private final AStarHeuristic heuristic;
	private final Tile start, end;

	private PathNode first, last, complete, completeReverse;

	private final List<PathNode> openSet, closedSet, openSetReverse,
			closedSetReverse;
	private final Map<Tile, PathNode> nodeWorld, nodeWorldReverse;

	public SimpleAStarPathSearch(AStarPathSearchProvider provider,
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
			PathNode adjacent = getNode(current, adjacentLocation, reverse);

			if(closedSet.contains(adjacent) || !isWalkable(current, adjacent))
				continue;
			double cost = current.getGScore()
					+ heuristic.calculateGScore(this, adjacent, reverse);

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

	private PathNode getNode(PathNode current, Tile tile, boolean reverse) {
		PathNode node = (reverse ? nodeWorldReverse : nodeWorld).get(tile);
		if(node == null) {
			node = new BasicPathNode(this, tile);
			node.setPrevious(current);
			(reverse ? nodeWorldReverse : nodeWorld).put(tile, node);
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
