package org.darkstorm.runescape.api.util;

import org.darkstorm.runescape.api.*;

public class FixedTilePath implements TilePath {
	private final GameContext context;
	private final Tile[] tiles;

	public FixedTilePath(GameContext context, Tile... tiles) {
		this.context = context;
		this.tiles = tiles.clone();
	}

	@Override
	public Tile getStart() {
		return tiles[0];
	}

	@Override
	public Tile getEnd() {
		return tiles[tiles.length - 1];
	}

	@Override
	public Tile getNext() {
		Tile location = context.getPlayers().getSelf().getLocation();
		int closestIndex = -1;
		double closestDistance = 0;
		for(int i = 0; i < tiles.length; i++) {
			double distance = location.distanceTo(tiles[i]);
			if(closestIndex == -1 || distance < closestDistance) {
				closestIndex = i;
				closestDistance = distance;
			}
		}
		if(closestIndex == -1)
			return null;
		Tile last = null;
		for(int i = closestIndex; i < tiles.length; i++) {
			Tile current = tiles[i];
			if(location.distanceTo(current) >= 15)
				break;
			last = current;
		}
		return last;
	}

	@Override
	public boolean isValid() {
		return tiles.length > 0
				&& getNext() != null
				&& !context.getPlayers().getSelf().getLocation()
						.equals(getEnd());
	}

	public FixedTilePath reverse() {
		Tile[] reversed = new Tile[tiles.length];
		for(int i = 0; i < tiles.length; i++)
			reversed[(tiles.length - 1) - i] = tiles[i];
		return new FixedTilePath(context, reversed);
	}

	public FixedTilePath randomize(int maxX, int maxY) {
		Calculations calc = context.getCalculations();
		Tile[] randomized = new Tile[tiles.length];
		for(int i = 0; i < tiles.length; ++i) {
			Tile tile = tiles[i];
			int randomX = (int) (tile.getX() + calc.random(0D, maxX) / 2D);
			int randomY = (int) (tile.getY() + calc.random(0D, maxY) / 2D);
			randomized[i] = new Tile(randomX, randomY);
		}
		return new FixedTilePath(context, randomized);
	}

	public Tile[] getTiles() {
		return tiles.clone();
	}
}
