package org.darkstorm.runescape.api.pathfinding;

import org.darkstorm.runescape.api.util.Tile;

public interface PathNode {
	public Tile getLocation();

	public PathNode getNext();

	public PathNode getPrevious();

	public double getGScore();

	public double getFScore();

	public void setNext(PathNode node);

	public void setPrevious(PathNode node);

	public void setGScore(double gScore);

	public void setFScore(double fScore);

	public boolean isStart();

	public boolean isEnd();

	public PathSearch getSource();
}
