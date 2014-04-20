package org.darkstorm.runescape.ui.debug;

import java.awt.*;
import java.awt.event.MouseEvent;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.event.EventHandler;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.event.input.MouseInputEvent;

public class MouseDebug extends Debug {
	private Point lastPress, current = new Point(0, 0);
	private long lastPressTime;
	private boolean pressed, dragging;

	public MouseDebug(Bot bot) {
		super(bot);
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		Graphics2D g = (Graphics2D) event.getGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
				RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
				RenderingHints.VALUE_ANTIALIAS_ON);
		// g.setStroke(new BasicStroke(2, BasicStroke.JOIN_ROUND,
		// BasicStroke.CAP_ROUND));

		long time = System.currentTimeMillis() - lastPressTime;
		if(dragging) {
			g.setColor(Color.RED);
			drawX(g, lastPress.x, lastPress.y, 16, 16);
			drawX(g, current.x, current.y, 16, 16);
			g.drawLine(lastPress.x, lastPress.y, current.x, current.y);
		} else if(lastPress != null && (pressed || time < 1000)) {
			g.setColor(time < 700 ? Color.RED : new Color(255, 0, 0,
					(int) (255D * (300 - (time - 700)) / 300D)));
			drawX(g, lastPress.x, lastPress.y, 16, 16);
		}
		g.setColor(new Color(252, 255, 0));
		drawX(g, current.x, current.y, 16, 16);
	}

	private void drawX(Graphics2D g, int x, int y, int width, int height) {
		x -= width / 2;
		y -= height / 2;
		g.drawLine(x, y, x + width, y + height);
		g.drawLine(x + width, y, x, y + height);
	}

	@EventHandler
	public void onPress(MouseInputEvent event) {
		MouseEvent e = event.getInputEvent();
		switch(e.getID()) {
		case MouseEvent.MOUSE_PRESSED:
			lastPress = e.getPoint();
			pressed = true;
			break;
		case MouseEvent.MOUSE_RELEASED:
			if(dragging)
				lastPress = e.getPoint();
			pressed = false;
			dragging = false;
			lastPressTime = System.currentTimeMillis();
			break;
		case MouseEvent.MOUSE_DRAGGED:
			dragging = true;
		case MouseEvent.MOUSE_MOVED:
			current = e.getPoint();
		}
	}
}
