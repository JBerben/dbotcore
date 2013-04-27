package org.darkstorm.runescape.api.input;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.logging.Logger;

import javax.swing.*;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.Menu;
import org.darkstorm.runescape.api.tab.Tab;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.script.*;

public class KBot2MouseTester extends Canvas implements Bot, GameContext,
		Calculations, MouseListener, MouseMotionListener, KeyListener {
	private static final long serialVersionUID = 3986612195690367009L;
	private BufferedImage image;
	private Graphics2D g;
	private EventManager eventManager = new BasicEventManager();

	// private KBot2Mouse mouse;
	private Mouse apiMouse = new BasicMouse(this);
	private Point dest;

	public KBot2MouseTester() {
		JFrame canvas = new JFrame("Canvas");
		setSize(765, 528);
		setIgnoreRepaint(true);
		canvas.add(this);
		canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		canvas.pack();
		canvas.setVisible(true);
		requestFocus();

		Timer timer = new Timer(20, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
		addMouseListener(this);
		addMouseMotionListener(this);
		addKeyListener(this);

		// mouse = new KBot2Mouse(this);
		while(true) {
			// mouse.setMouseSpeed(random(1, 10));
			// Point current = mouse.getMousePos();
			int width = 765, height = 503;
			Point p = new Point(random(0, width), random(0, height));
			apiMouse.move(new PointMouseTarget(p));
			apiMouse.await();
			// double dist = current.distance(p);
			// double maxDist = Math.sqrt(width * width + height * height);
			// double factor = 7 * ((maxDist - dist) / maxDist);
			// factor = random(Math.max(factor - 2, 1), factor + 2);
			// dest = p;
			// mouse.setMouseSpeed(factor);
			// System.out.println(factor);
			// mouse.moveMouse(p, 0, 0);
		}
	}

	@Override
	public void paint(Graphics g) {
		update(g);
	}

	@Override
	public void update(Graphics graphics) {
		if(image == null) {
			image = (BufferedImage) createImage(getWidth(), getHeight());
			g = image.createGraphics();
		}
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
		eventManager.sendEvent(new PaintEvent(g, image.getWidth(), image
				.getHeight()));
		if(dest != null) {
			g.setColor(new Color(0, 200, 0));
			g.drawLine(dest.x, 0, dest.x, image.getHeight());
			g.drawLine(0, dest.y, image.getWidth(), dest.y);
		}
		Point mpos = apiMouse.getLocation();// mouse.getMousePos();
		g.setColor(Color.BLACK);
		g.drawLine(mpos.x, 0, mpos.x, image.getHeight());
		g.drawLine(0, mpos.y, image.getWidth(), mpos.y);
		g.drawString("Point: (" + mpos.x + ", " + mpos.y + ")", 5, g
				.getFontMetrics().getHeight());
		// g.drawString("Speed: " + mouse.getSpeed(), 5, g.getFontMetrics()
		// .getHeight() * 2 + 5);
		graphics.drawImage(image, 0, 0, null);
	}

	public static void main(String[] args) {
		new KBot2MouseTester();
	}

	@Override
	public GameContext getContext() {
		return this;
	}

	@Override
	public boolean isInGameArea(Point point) {
		return false;
	}

	@Override
	public boolean isInGameArea(int x, int y) {
		return false;
	}

	@Override
	public Shape getGameArea() {
		return null;
	}

	@Override
	public boolean isOnScreen(Point point) {
		return false;
	}

	@Override
	public boolean isOnScreen(int x, int y) {
		return false;
	}

	@Override
	public Rectangle getScreenArea() {
		return null;
	}

	@Override
	public boolean canReach(Tile tile) {
		return false;
	}

	@Override
	public int random(int min, int max) {
		return min + (int) (Math.random() * (max - min));
	}

	@Override
	public double random(double min, double max) {
		return min + (Math.random() * (max - min));
	}

	@Override
	public Point getTileScreenLocation(Tile tile) {
		return null;
	}

	@Override
	public Point getTileMinimapLocation(Tile tile) {
		return null;
	}

	@Override
	public Point getWorldScreenLocation(double x, double y, int height) {
		return null;
	}

	@Override
	public Point getLimitlessWorldScreenLocation(double x, double y, int height) {
		return null;
	}

	@Override
	public Point getWorldMinimapLocation(int x, int y) {
		return null;
	}

	@Override
	public int getTileHeight(Tile tile) {
		return 0;
	}

	@Override
	public int getWorldHeight(double x, double y, int plane) {
		return 0;
	}

	@Override
	public Calculations getCalculations() {
		return this;
	}

	@Override
	public Players getPlayers() {
		return null;
	}

	@Override
	public NPCs getNPCs() {
		return null;
	}

	@Override
	public Mouse getMouse() {
		return null;
	}

	@Override
	public Keyboard getKeyboard() {
		return null;
	}

	@Override
	public Interfaces getInterfaces() {
		return null;
	}

	@Override
	public GroundItems getGroundItems() {
		return null;
	}

	@Override
	public GameObjects getGameObjects() {
		return null;
	}

	@Override
	public Skills getSkills() {
		return null;
	}

	@Override
	public Menu getMenu() {
		return null;
	}

	@Override
	public Bank getBank() {
		return null;
	}

	@Override
	public Inventory getInventory() {
		return null;
	}

	@Override
	public Camera getCamera() {
		return null;
	}

	@Override
	public Walking getWalking() {
		return null;
	}

	@Override
	public Filters getFilters() {
		return null;
	}

	@Override
	public Bot getBot() {
		return this;
	}

	@Override
	public String getName() {
		return null;
	}

	@Override
	public InputState getInputState() {
		return null;
	}

	@Override
	public void setInputState(InputState state) {
	}

	@Override
	public boolean canPlayScript() {
		return false;
	}

	@Override
	public Component getDisplay() {
		return null;
	}

	@Override
	public Asdf getGame() {
		return new Asdf();
	}

	@Override
	public Canvas getCanvas() {
		return this;
	}

	@Override
	public Logger getLogger() {
		return null;
	}

	@Override
	public void dispatchInputEvent(InputEvent event) {
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public ScriptManager getScriptManager() {
		return null;
	}

	@Override
	public GameContext getGameContext() {
		return null;
	}

	@Override
	public DarkBot getDarkBot() {
		return null;
	}

	private class Asdf extends Applet implements Game {
		private static final long serialVersionUID = -5577254310359758849L;

		@Override
		public int getWidth() {
			return 765;
		}

		@Override
		public int getHeight() {
			return 503;
		}

		@Override
		public GameContext getContext() {
			return null;
		}

		@Override
		public Tab[] getTabs() {
			return null;
		}

		@Override
		public Tab getOpenTab() {
			return null;
		}

		@Override
		public Tab getTab(String name) {
			return null;
		}

		@Override
		public <T extends Tab> T getTab(Class<T> tabClass) {
			return null;
		}

		@Override
		public int getRegionBaseX() {
			return 0;
		}

		@Override
		public int getRegionBaseY() {
			return 0;
		}

		@Override
		public int getHealthPercentage() {
			return 0;
		}

		@Override
		public int getHealth() {
			return 0;
		}

		@Override
		public int getMaxHealth() {
			return 0;
		}

		@Override
		public int getRunPercentage() {
			return 0;
		}

		@Override
		public int getPrayerPercentage() {
			return 0;
		}

		@Override
		public int getPrayerPoints() {
			return 0;
		}

		@Override
		public int getMaxPrayerPoints() {
			return 0;
		}

		@Override
		public void enableQuickPrayers() {
		}

		@Override
		public void disableQuickPrayers() {
		}

		@Override
		public boolean isUsingQuickPrayers() {
			return false;
		}

		@Override
		public boolean hasSelectedItem() {
			return false;
		}

		@Override
		public boolean hasDestination() {
			return false;
		}

		@Override
		public Tile getDestination() {
			return null;
		}

		@Override
		public int getCurrentFloor() {
			return 0;
		}

		@Override
		public GameState getGameState() {
			return null;
		}

		@Override
		public boolean isInFixedMode() {
			return false;
		}

		@Override
		public boolean isLoading() {
			return false;
		}

		@Override
		public boolean isLoggedIn() {
			return false;
		}

		@Override
		public boolean isInLobby() {
			return false;
		}

		@Override
		public void logout() {
		}

		@Override
		public void logout(boolean toLobby) {
		}

		@Override
		public int[][] getTileCollisionData() {
			return null;
		}

	}

	@Override
	public void keyTyped(KeyEvent e) {
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}

	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}

	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
	}

	@Override
	public void mouseReleased(MouseEvent e) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
	}

	@Override
	public void mouseExited(MouseEvent e) {
	}

	@Override
	public void sleep(int time) {
	}

	@Override
	public void sleep(int min, int max) {
	}

	@Override
	public RandomEventManager getRandomEventManager() {
		return null;
	}

	@Override
	public Settings getSettings() {
		return null;
	}

	@Override
	public double distanceBetween(Tile tile1, Tile tile2) {
		return 0;
	}

	@Override
	public double realDistanceBetween(Tile tile1, Tile tile2) {
		return 0;
	}

	@Override
	public TilePath generatePath(Tile start, Tile end) {
		return null;
	}
}
