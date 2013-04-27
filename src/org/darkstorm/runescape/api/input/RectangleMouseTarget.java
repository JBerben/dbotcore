package org.darkstorm.runescape.api.input;

import java.awt.*;

public class RectangleMouseTarget implements MouseTarget {
	private final Rectangle area;

	public RectangleMouseTarget(Rectangle area) {
		area = new Rectangle(area);
		this.area = area;
	}

	@Override
	public Point getLocation() {
		return new Point(area.x + (int) (random() * area.width), area.y
				+ (int) (random() * area.height));
	}

	private double random() {
		return (raisedCosine(Math.random(), 1.0, 0.5) / 2)
				+ (0.5 - (raisedCosine(Math.random(), 1.0, 0.5) / 2));
	}

	private double raisedCosine(double x, double s, double o) {
		return (1D / (2D * s)) * (1D + Math.cos(((x - o) / s) * Math.PI));
	}

	@Override
	public boolean isOver(Point point) {
		return area.contains(point);
	}

	@Override
	public MouseTarget getTarget() {
		return this;
	}

	@Override
	public void render(Graphics g) {
		g.drawRect(area.x, area.y, area.width, area.height);
	}
}
