package org.darkstorm.runescape.api.input;

import java.util.Random;

import java.awt.Point;

public class PointMouseTarget implements MouseTarget {
	private final Point point;
	private final int randomness;
	private final Point randomized;

	public PointMouseTarget(Point point) {
		this(point, 3);
	}

	public PointMouseTarget(Point point, int randomness) {
		this.point = point;
		this.randomness = randomness;

		Random random = new Random();
		double randomDegrees = random.nextDouble() * 2 * Math.PI;
		double randomFactorX = random.nextDouble() * 2 - 1;
		double randomFactorY = random.nextDouble() * 2 - 1;
		randomized = new Point(
				(int) (randomness * randomFactorX * Math.cos(randomDegrees)),
				(int) (randomness * randomFactorY * Math.sin(randomDegrees)));
	}

	@Override
	public Point getLocation() {
		return randomized;
	}

	@Override
	public boolean isOver(Point point) {
		return Math.sqrt(Math.pow(point.x - this.point.x, 2)
				+ Math.pow(point.y - this.point.y, 2)) <= randomness;
	}
}
