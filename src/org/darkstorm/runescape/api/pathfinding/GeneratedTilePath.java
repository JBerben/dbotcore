package org.darkstorm.runescape.api.pathfinding;

import java.util.*;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.api.util.*;

@SuppressWarnings("unused")
public class GeneratedTilePath implements TilePath {
	private final GameContext context;
	private final PathNode path;

	private FixedTilePath fixedPath;

	public GeneratedTilePath(GameContext context, PathNode path) {
		this.context = context;
		this.path = path;

		List<Tile> tiles = new ArrayList<>();
		PathNode current = path, next = path.getNext();
		while(next != null) {
			tiles.add(current.getLocation());
			current = next;
			next = current.getNext();
		}
		tiles.add(current.getLocation());
		fixedPath = new FixedTilePath(context, tiles.toArray(new Tile[tiles
				.size()]));
	}

	@Override
	public Tile getStart() {
		return fixedPath.getStart();
	}

	@Override
	public Tile getEnd() {
		return fixedPath.getEnd();
	}

	@Override
	public Tile getNext() {
		return fixedPath.getNext();
	}

	@Override
	public boolean isValid() {
		return fixedPath.isValid();
	}

	public FixedTilePath getFixedPath() {
		return fixedPath;
	}

}
