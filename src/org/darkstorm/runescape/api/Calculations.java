package org.darkstorm.runescape.api;

import java.awt.*;

import org.darkstorm.runescape.api.util.Tile;

public interface Calculations extends Utility {
	public boolean isInGameArea(Point point);

	public Shape getGameArea();

	public boolean isOnScreen(Point point);

	public boolean isOnScreen(int x, int y);

	public Rectangle getScreenArea();

	public boolean canReach(Tile tile);

	public int random(int min, int max);

	public double random(double min, double max);

	public Point getTileScreenLocation(Tile tile);

	public Point getTileMinimapLocation(Tile tile);

	public Point getWorldScreenLocation(int x, int y, int height);

	public Point getLimitlessWorldScreenLocation(int x, int y, int height);

	public Point getWorldMinimapLocation(int x, int y);

	public int getTileHeight(Tile tile);

	public int getWorldHeight(int x, int y, double dx, double dy, int plane);
}
