package org.darkstorm.runescape.api.util;

public interface TilePath {
	public Tile getStart();

	public Tile getEnd();

	public Tile getNext();

	public boolean isValid();
}
