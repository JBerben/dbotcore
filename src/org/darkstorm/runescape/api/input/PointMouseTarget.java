package org.darkstorm.runescape.api.input;

import java.awt.*;
import java.util.Random;

public class PointMouseTarget implements MouseTarget {
	private final Point point;
	private final int randomness;

	public PointMouseTarget(Point point) {
		this(point, 3);
	}

	public PointMouseTarget(Point point, int randomness) {
		point = new Point(point);
		this.point = point;
		this.randomness = randomness;
	}

	@Override
	public Point getLocation() {
		Random random = new Random();
		double randomDegrees = random.nextDouble() * 2 * Math.PI;
		double randomFactorX = random.nextDouble() * 2 - 1;
		double randomFactorY = random.nextDouble() * 2 - 1;
		return new Point(point.x
				+ (int) (randomness * randomFactorX * Math.cos(randomDegrees)),
				point.y
						+ (int) (randomness * randomFactorY * Math
								.sin(randomDegrees)));
	}

	@Override
	public boolean isOver(Point point) {
		return Math.sqrt(Math.pow(point.x - this.point.x, 2)
				+ Math.pow(point.y - this.point.y, 2)) <= randomness;
	}

	@Override
	public MouseTarget getTarget() {
		return this;
	}

	@Override
	public void render(Graphics g) {
		g.drawOval(point.x - 2 - randomness / 2, point.y - 2 - randomness / 2,
				4 + randomness, 4 + randomness);
	}
}
