package org.darkstorm.runescape.api.input;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.*;

import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.event.game.PaintEvent;

/**
 * Created by IntelliJ IDEA. User: Jan Ove / Kosaki Date: 22.mar.2009 Time:
 * 15:59:15
 */
public class KBot2Mouse implements EventListener {
	private final GameContext context;
	private final BufferedImage buffImage;
	private final Graphics2D buffGraphics;
	private int n = 4;
	private final int w;
	private final int h;
	private DoublePoint[] points;
	private final Random random = new Random();
	public final java.util.List<Line> splineLines = new LinkedList<Line>();
	private final java.util.List<Line> outlineLines = new LinkedList<Line>();
	private final java.util.List<Point> outLinePoints = new LinkedList<Point>();
	private double speed = 7;

	private Point mouseLocation = new Point(0, 0);

	public KBot2Mouse(GameContext context) {
		this.context = context;
		w = 765;
		h = 503;
		buffImage = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		buffGraphics = buffImage.createGraphics();
		buffGraphics.setBackground(new Color(0, 0, 0, 0));

		context.getBot().getEventManager().registerListener(this);
	}

	private double getSpeed(int percentage) {
		/**
		 * Function for speed: f(x) = 0.001x^2+0.01x
		 */
		double a = random.nextDouble() * 0.01 + 0.0001;
		double b = random.nextDouble() * 0.03 + 0.005;
		double speedFactor = a * Math.pow(percentage, 2) + b * percentage;
		return speed + speedFactor;
	}

	private void splineMouse() {
		Line line1 = splineLines.get(0);
		int lastX = line1.x1;
		int lastY = line1.y1;
		if(line1.x1 < 0 || line1.y1 < 0 || line1.x1 > 766 || line1.y1 > 504) {
			lastX = -1;
			lastY = -1;
		}
		int x;
		int y;
		for(int i = 0; i < splineLines.size(); i++) {
			int percentage = i / splineLines.size();
			speed = getSpeed(percentage);
			Line line = splineLines.get(i);
			x = line.x2;
			y = line.y2;
			if(lastX != x || lastY != y
					&& (x > -1 && y > -1 && x < 766 && y < 504)) {
				moveMouseInternal(lastX, lastY, x, y);
				lastX = x;
				lastY = y;
			} else {
				if(lastX != x || lastY != y) {
					try {
						int secs = (int) speed;
						double nanos = speed - secs;
						int nanosReal = (int) (nanos * 1000);
						Thread.sleep(secs, nanosReal);
					} catch(InterruptedException ignored) {
						throw new ThreadDeath();
					}
				}
			}
		}
		buffGraphics.setComposite(AlphaComposite.Clear);
		buffGraphics.fillRect(0, 0, w, h);
		// reset alpha composite
		buffGraphics.setComposite(AlphaComposite.SrcOver);
	}

	/**
	 * @param n
	 * @return returns a "humanlike" control point
	 * @author PwnZ
	 */
	private double createSmartControlPoint(final int n, final double spacing,
			final boolean yValue, final int distance) {
		final int length = (int) spacing;
		double d;
		if(yValue) {
			d = random(0, length - n) * random.nextDouble() * random(0, 2);
		} else {
			d = random(0, length - n) * random.nextDouble()
					* random(distance / 100, distance / 100 + random(2, 4));
		}
		return d;
	}

	private void createSpline(Point start, Point end) {
		int distance = (int) Point2D.distance(start.x, start.y, end.x, end.y);
		n = (distance / 100) + random.nextInt(3) + 4;
		if(distance < 100)
			n = 3;
		points = new DoublePoint[n];

		points[0] = new DoublePoint(start.x, start.y);
		points[n - 1] = new DoublePoint(end.x, end.y);

		int midPoints = n - 2;
		DoublePoint lastPos = new DoublePoint(points[0].x, points[0].y);
		for(int i = 1; i < n - 1; i++) {
			double X = lastPos.x;
			double Y = lastPos.y;
			double spacing = distance / (midPoints + 2);
			int randomNum = random.nextInt(2);
			if(randomNum == 0) {
				X += createSmartControlPoint(i, spacing, false, distance)/**
				 * 
				 * (random.nextInt(2) == 0? -1:1)
				 */
				;
			} else {
				X -= createSmartControlPoint(i, spacing, false, distance)/**
				 * 
				 * (random.nextInt(2) == 0? -1:1)
				 */
				;
			}

			randomNum = random.nextInt(2);
			if(randomNum == 0) {
				Y += createSmartControlPoint(i, spacing, true, distance)/**
				 * 
				 * (random.nextInt(2) == 0? -1:1)
				 */
				;
			} else {
				Y -= createSmartControlPoint(i, spacing, true, distance)/**
				 * 
				 * (random.nextInt(2) == 0? -1:1)
				 */
				;
			}
			points[i] = new DoublePoint(X, Y);
			lastPos.x = X;
			lastPos.y = Y;
		}
		generateSpline();
	}

	public void paintSpline() {
		buffGraphics.setComposite(AlphaComposite.Clear);
		buffGraphics.fillRect(0, 0, w, h);
		// reset alpha composite
		buffGraphics.setComposite(AlphaComposite.SrcOver);

		buffGraphics.setColor(Color.blue);
		for(Point p : outLinePoints) {
			buffGraphics.drawRect(p.x - 2, p.y - 2, 4, 4);
		}
		for(Line l : outlineLines) {
			buffGraphics.drawLine(l.x1, l.y1, l.x2, l.y2);
		}
		buffGraphics.setColor(Color.red);
		for(Line l : splineLines) {
			buffGraphics.drawLine(l.x1, l.y1, l.x2, l.y2);
		}
	}

	private void generateSpline() {
		outlineLines.clear();
		outLinePoints.clear();
		splineLines.clear();

		double step = 1. / w, t = step;
		DoublePoint[] points2 = new DoublePoint[n];
		int X, Y, Xold = (int) points[0].x, Yold = (int) points[0].y;
		for(int i = 0; i < n; i++) {
			X = (int) points[i].x;
			Y = (int) points[i].y;
			outLinePoints.add(new Point(X, Y));
		}
		if(n > 2) {
			int Xo = Xold, Yo = Yold;
			for(int i = 1; i < n; i++) {
				X = (int) points[i].x;
				Y = (int) points[i].y;
				outlineLines.add(new Line(Xo, Yo, X, Y));
				Xo = X;
				Yo = Y;
			}
		}
		for(int k = 1; k < w; k++) {
			System.arraycopy(points, 0, points2, 0, n);

			for(int j = n - 1; j > 0; j--)
				// points calculation
				for(int i = 0; i < j; i++) {
					points2[i].x = (1 - t) * points2[i].x + t
							* points2[i + 1].x;
					points2[i].y = (1 - t) * points2[i].y + t
							* points2[i + 1].y;
				}

			X = (int) points2[0].x;
			Y = (int) points2[0].y;
			splineLines.add(new Line(Xold, Yold, X, Y));
			Xold = X;
			Yold = Y;
			t += step;
		}
		paintSpline();
	}

	public void setMousePos(int x, int y) {
		if(x < 0) {
			x = -1;
		}
		if(y < 0) {
			y = -1;
		}
		if(x > 765) {
			x = -1;
		}
		if(y > 504)
			y = -1;
		mouseLocation.x = x;
		mouseLocation.y = y;
		// System.out.println("Mouse moved to (" + x + ", " + y + ")");
		context.getBot().dispatchInputEvent(
				new MouseEvent(context.getBot().getGame(),
						MouseEvent.MOUSE_MOVED, System.currentTimeMillis(), 0,
						x, y, 0, false, 0));
	}

	private void moveMouseInternal(int curX, int curY, int x, int y) {
		double distance = Point2D.distance(curX, curY, x, y);
		while(distance > 0) {
			if(curX < 0 || curY < 0) {
				curX = x;
				curY = y;
			}
			if(Math.round(curX) < Math.round(x))
				curX++;
			else if(Math.round(curX) > Math.round(x))
				curX--;
			if(Math.round(curY) < Math.round(y))
				curY++;
			else if(Math.round(curY) > Math.round(y))
				curY--;
			setMousePos(curX, curY);
			try {
				int secs = (int) speed;
				double nanos = speed - secs;
				int nanosReal = (int) (nanos * 1000);
				Thread.sleep(secs, nanosReal);
			} catch(InterruptedException e) {
				throw new ThreadDeath();
			}
			distance = Point2D.distance(curX, curY, x, y);
		}
	}

	public void moveMouse(int x, int y, int randomX, int randomY) {
		int thisX = mouseLocation.x, thisY = mouseLocation.y;
		if(thisX < 0 || thisY < 0) {
			switch(random(1, 5)) { // on which side of canvas should it enter
			case 1:
				thisX = 1;
				thisY = random(0, 500);
				setMousePos(thisX, thisY);
				break;
			case 2:
				thisX = random(0, 765);
				thisY = 501;
				setMousePos(thisX, thisY);
				break;
			case 3:
				thisX = 766;
				thisY = random(0, 500);
				setMousePos(thisX, thisY);
				break;
			case 4:
				thisX = random(0, 765);
				thisY = 1;
				setMousePos(thisX, thisY);
				break;
			}
		}
		if(thisX == x && thisY == y) {
			return;
		}
		if(Point2D.distanceSq(thisX, thisY, x, y) < 10) {
			splineLines.clear();
			splineLines.add(new Line(thisX, thisY, random(x, x + randomX),
					random(y, y + randomY)));
			paintSpline();
		} else {
			createSpline(new Point(thisX, thisY),
					new Point(random(x, x + randomX), random(y, y + randomY)));
		}
		splineMouse();
	}

	public void moveMouse(int x, int y) {
		moveMouse(x, y, 0, 0);
	}

	public void pressMouse(int x, int y, boolean button) {
		if(x < 0 || y < 0 || x > 756 || y > 503)
			return;
		context.getBot().dispatchInputEvent(
				new MouseEvent(context.getBot().getGame(),
						MouseEvent.MOUSE_PRESSED, System.currentTimeMillis(),
						0, x, y, 1, false, button ? MouseEvent.BUTTON1
								: MouseEvent.BUTTON3));
	}

	public void releaseMouse(int x, int y, boolean button) {
		if(x < 0 || y < 0 || x > 756 || y > 503)
			return;
		context.getBot().dispatchInputEvent(
				new MouseEvent(context.getBot().getGame(),
						MouseEvent.MOUSE_RELEASED, System.currentTimeMillis(),
						0, x, y, 1, false, button ? MouseEvent.BUTTON1
								: MouseEvent.BUTTON3));

		context.getBot().dispatchInputEvent(
				new MouseEvent(context.getBot().getGame(),
						MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(),
						0, x, y, 1, false, button ? MouseEvent.BUTTON1
								: MouseEvent.BUTTON3));
	}

	/**
	 * Moves the mouse with randomness and clicks.
	 * 
	 * @param p
	 * @param randomX
	 * @param randomY
	 * @param button
	 */
	public void clickMouse(Point p, int randomX, int randomY, boolean button) {
		moveMouse(p, randomX, randomY);
		sleep(random(50, 150));
		clickMouse(button);
	}

	/**
	 * Moves the mouse and clicks at the given position.
	 * 
	 * @param p
	 * @param button
	 */
	public void clickMouse(Point p, boolean button) {
		clickMouse(p, 0, 0, button);
	}

	/**
	 * Moves the mouse with randomness
	 * 
	 * @param p
	 * @param randomX
	 * @param randomY
	 */
	public void moveMouse(Point p, int randomX, int randomY) {
		moveMouse(p.x, p.y, randomX, randomY);
	}

	private void clickMouse(int x, int y, boolean button) {
		pressMouse(x, y, button);
		sleep(random(0, 70));
		releaseMouse(x, y, button);
	}

	public void clickMouse(boolean button) {
		clickMouse(mouseLocation.x, mouseLocation.y, button);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		Graphics g = event.getGraphics();
		g.drawImage(buffImage, 0, 0, null);
	}

	public void setMouseSpeed(double speed) {
		this.speed = speed;
	}

	private class DoublePoint {
		public double x;
		public double y;

		public DoublePoint(double x, double y) {
			this.x = x;
			this.y = y;
		}
	}

	public class Line {
		public final int x1;
		public final int y1;
		public final int x2;
		public final int y2;

		public Line(int x1, int y1, int x2, int y2) {
			this.x1 = x1;
			this.y1 = y1;
			this.x2 = x2;
			this.y2 = y2;
		}
	}

	public void dragMouse(Point destination, int randomX, int randomY) {
		int thisX = mouseLocation.x, thisY = mouseLocation.y;
		pressMouse(thisX, thisY, true);
		sleep(random(10, 50));
		moveMouse(destination, randomX, randomY);
		sleep(random(10, 50));
		clickMouse(thisX, thisY, true);
	}

	public void dragMouse(Point destination) {
		dragMouse(destination, 0, 0);
	}

	/**
	 * VERY Human like method - great for anti bans! This will move the mouse
	 * around the screen at a random distance between 1 and maxDistance, but
	 * will sometimes move it more than one, like a human would, resulting in
	 * cool effects like cursor circling and more.
	 * 
	 * @param maxDistance
	 * @return true if it is going to call on itself again, false otherwise
	 *         (returns false to you every time)
	 */
	public boolean moveMouseRandomly(int maxDistance) {
		if(maxDistance == 0) {
			return false;
		}
		maxDistance = random(1, maxDistance);
		Point p = new Point(getRandomMouseX(maxDistance),
				getRandomMouseY(maxDistance));
		if(p.x < 1 || p.y < 1) {
			p.x = p.y = 1;
		}
		moveMouse(p.x, p.y);
		if(random(0, 2) == 0) {
			return false;
		}
		return moveMouseRandomly(maxDistance / 2);
	}

	/**
	 * Gives a X position on the screen within the maxDistance.
	 * 
	 * @param maxDistance
	 *            the maximum distance the cursor will move on the X axis
	 * @return A random int that represents a X coordinate for the
	 *         moveMouseRandomly method.
	 */
	public int getRandomMouseX(int maxDistance) {
		Point p = getMousePos();
		if(random(0, 2) == 0) {
			return p.x - random(0, p.x < maxDistance ? p.x : maxDistance);
		} else {
			return p.x
					+ random(1, (762 - p.x < maxDistance) ? 762 - p.x
							: maxDistance);
		}
	}

	/**
	 * Gives a Y position on the screen within the maxDistance.
	 * 
	 * @param maxDistance
	 *            the maximum distance the cursor will move on the Y axis
	 * @return A random int that represents a Y coordinate for the
	 *         moveMouseRandomly method.
	 */
	public int getRandomMouseY(int maxDistance) {
		Point p = getMousePos();
		if(random(0, 2) == 0) {
			return p.y - random(0, p.y < maxDistance ? p.y : maxDistance);
		} else {
			return p.y
					+ random(1, (500 - p.y < maxDistance) ? 500 - p.y
							: maxDistance);
		}
	}

	public double getSpeed() {
		return speed;
	}

	/**
	 * Gets the mouse position
	 * 
	 * @return Point with coords.
	 */
	public Point getMousePos() {
		return new Point(mouseLocation);
	}

	private int random(int min, int max) {
		return context.getCalculations().random(min, max);
	}

	private void sleep(int time) {
		context.getCalculations().sleep(time);
	}
}
