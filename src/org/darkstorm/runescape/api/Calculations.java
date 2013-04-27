package org.darkstorm.runescape.api;

import java.awt.*;

import org.darkstorm.runescape.api.util.*;

public interface Calculations extends Utility {
	public boolean isInGameArea(Point point);

	public boolean isInGameArea(int x, int y);

	public Shape getGameArea();

	public boolean isOnScreen(Point point);

	public boolean isOnScreen(int x, int y);

	public Rectangle getScreenArea();

	public boolean canReach(Tile tile);

	public int random(int min, int max);

	public double random(double min, double max);

	public void sleep(int time);

	public void sleep(int min, int max);

	public Point getTileScreenLocation(Tile tile);

	public Point getTileMinimapLocation(Tile tile);

	public Point getWorldScreenLocation(double x, double y, int height);

	public Point getLimitlessWorldScreenLocation(double x, double y, int height);

	public Point getWorldMinimapLocation(int x, int y);

	public int getTileHeight(Tile tile);

	public int getWorldHeight(double x, double y, int plane);

	public double distanceBetween(Tile tile1, Tile tile2);

	public double realDistanceBetween(Tile tile1, Tile tile2);

	public TilePath generatePath(Tile start, Tile end);
}
