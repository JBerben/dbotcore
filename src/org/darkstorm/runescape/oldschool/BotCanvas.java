package org.darkstorm.runescape.oldschool;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.InputEvent;
import java.awt.image.*;
import java.lang.reflect.Method;

import javax.swing.Timer;

import org.darkstorm.runescape.InputState;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.event.input.*;

@SuppressWarnings("serial")
public class BotCanvas extends Canvas implements ComponentListener,
		MouseListener, MouseMotionListener, KeyListener {
	private final OldSchoolBot bot;
	private final Timer timer;

	private final BufferedImage intermediateImage;

	public BotCanvas(OldSchoolBot bot) {
		this.bot = bot;
		setIgnoreRepaint(true);
		BufferedImage gameImage = bot.getGameImage();
		setPreferredSize(new Dimension(gameImage.getWidth(),
				gameImage.getHeight()));
		setSize(getPreferredSize());
		intermediateImage = new BufferedImage(gameImage.getWidth(),
				gameImage.getHeight(), BufferedImage.TYPE_INT_RGB);

		timer = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();

		addComponentListener(this);
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);
	}

	@Override
	public void paintAll(Graphics g) {
		update(g);
	}

	@Override
	public void paint(Graphics g) {
		update(g);
	}

	@Override
	public void update(Graphics g) {
		if(!isVisible())
			return;
		try {
			Rectangle r = g.getClipBounds();

			Graphics graphics = intermediateImage.createGraphics();
			BufferedImage botImage = bot.getBotImage();
			BufferedImage gameImage = bot.getGameImage();
			graphics.setColor(Color.BLACK);
			graphics.fillRect(0, 0, intermediateImage.getWidth(),
					intermediateImage.getHeight());

			if(bot.getGame() != null) {
				int width = gameImage.getWidth(), height = gameImage
						.getHeight();
				WritableRaster raster = botImage.getRaster();
				raster.setPixels(0, 0, width, height, new int[width * height
						* raster.getNumBands()]);
				Graphics botGraphics = botImage.getGraphics();
				bot.getEventManager().sendEvent(
						new PaintEvent(botGraphics, width, height));
				botGraphics.dispose();

				gameImage.flush();
				graphics.drawImage(gameImage, r.x, r.y, r.x + r.width, r.y
						+ r.height, r.x, r.y, r.x + r.width, r.y + r.height,
						null);
			}

			gameImage.flush();
			graphics.drawImage(botImage, r.x, r.y, r.x + r.width, r.y
					+ r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
			intermediateImage.flush();
			g.drawImage(intermediateImage, r.x, r.y, r.x + r.width, r.y
					+ r.height, r.x, r.y, r.x + r.width, r.y + r.height, null);
		} catch(RasterFormatException ignored) {}
	}

	@Override
	public void componentResized(ComponentEvent e) {
	}

	@Override
	public void componentMoved(ComponentEvent e) {
	}

	@Override
	public void componentShown(ComponentEvent e) {
		if(!timer.isRunning())
			timer.start();
	}

	@Override
	public void componentHidden(ComponentEvent e) {
		if(timer.isRunning())
			timer.stop();
	}

	@Override
	public void keyTyped(KeyEvent e) {
		if(!bot.getInputState().equals(InputState.NONE))
			handleEvent(e);
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if(!bot.getInputState().equals(InputState.NONE))
			handleEvent(e);
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if(!bot.getInputState().equals(InputState.NONE))
			handleEvent(e);
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	@Override
	public void mouseExited(MouseEvent e) {
		if(bot.getInputState().equals(InputState.MOUSE_KEYBOARD))
			handleEvent(e);
	}

	public void handleEvent(InputEvent e) {
		Applet game = bot.getGame();
		if(game == null)
			return;
		try {
			Method method = Component.class.getDeclaredMethod("processEvent",
					AWTEvent.class);
			method.setAccessible(true);
			method.invoke(game, e);
			fireEventRecursively(game, method, e);
			if(e instanceof MouseEvent)
				bot.getEventManager().sendEvent(
						new MouseInputEvent((MouseEvent) e));
			else if(e instanceof KeyEvent)
				bot.getEventManager().sendEvent(
						new KeyboardInputEvent((KeyEvent) e));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
	}

	private void fireEventRecursively(Container container, Method method,
			AWTEvent e) throws Exception {
		for(Component component : container.getComponents()) {
			method.invoke(component, e);
			if(component instanceof Container)
				fireEventRecursively((Container) component, method, e);
		}
	}
}
