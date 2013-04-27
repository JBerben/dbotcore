package org.darkstorm.runescape.api.pathfinding;

import org.darkstorm.runescape.api.util.Tile;

public interface PathSearch {
	public void step();

	public boolean isDone();

	public Tile getStart();

	public Tile getEnd();

	public PathNode getPath();

	public PathSearchProvider getSource();
}
