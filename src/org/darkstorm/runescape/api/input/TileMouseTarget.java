package org.darkstorm.runescape.api.input;

import java.awt.*;

import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.util.Tile;

public class TileMouseTarget implements MouseTarget {
	private final GameContext context;
	private final Tile tile;
	private final double width, length;
	private final int height;

	public TileMouseTarget(GameContext context, Tile tile) {
		this(context, new Tile(tile.getX(), tile.getY(), tile.getPlane()),
				0.5D, 0.5D, 0D);
	}

	public TileMouseTarget(GameContext context, Tile tile, double width,
			double length, double height) {
		this.context = context;
		this.tile = tile;
		this.width = width;
		this.length = length;
		this.height = (int) (height * 128);
	}

	@Override
	public Point getLocation() {
		Polygon area = getArea();
		if(area == null)
			return null;
		for(int i = 0; i < 100; i++) {
			double x = tile.getPreciseX() - width / 2D + width * random();
			double y = tile.getPreciseY() - length / 2D + length * random();
			Point point = context.getCalculations().getWorldScreenLocation(x,
					y, height);
			if(area.contains(point)
					&& context.getCalculations().isInGameArea(point))
				return point;
		}
		return null;
	}

	private double random() {
		return (raisedCosine(Math.random(), 1.0, 0.5) / 2D)
				+ (0.5 - (raisedCosine(Math.random(), 1.0, 0.5) / 2D));
	}

	private double raisedCosine(double x, double s, double o) {
		return (1D / (2D * s)) * (1D + Math.cos(((x - o) / s) * Math.PI));
	}

	@Override
	public boolean isOver(Point point) {
		Polygon area = getArea();
		if(area == null)
			return false;
		return area.contains(point);
	}

	private Polygon getArea() {
		Point[] points = new Point[4];
		Calculations calc = context.getCalculations();
		double x = tile.getPreciseX() - width / 2D, y = tile.getPreciseY()
				- length / 2D;
		points[0] = calc.getWorldScreenLocation(x, y, height);
		points[1] = calc.getWorldScreenLocation(x + width, y, height);
		points[2] = calc.getWorldScreenLocation(x + width, y + length, height);
		points[3] = calc.getWorldScreenLocation(x, y + length, height);
		int[] xPoints = new int[4], yPoints = new int[4];
		for(int i = 0; i < 4; i++) {
			if(!context.getCalculations().isInGameArea(points[i]))
				return null;
			xPoints[i] = points[i].x;
			yPoints[i] = points[i].y;
		}
		return new Polygon(xPoints, yPoints, 4);
	}

	@Override
	public MouseTarget getTarget() {
		return this;
	}

	@Override
	public void render(Graphics g) {
		Polygon area = getArea();
		if(area == null)
			return;
		g.drawPolygon(area);
	}
}
