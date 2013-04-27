package org.darkstorm.runescape.api.wrapper;

import java.awt.*;

public interface Model extends Wrapper {
	public Polygon[] getTriangles();

	public Polygon getHull();

	public void draw(Graphics g);

	public void fill(Graphics g);

	public Point getRandomPointWithin();

	public Point getCenterPoint();

	public boolean contains(Point point);

	public int getOrientation();

	public boolean isValid();
}
