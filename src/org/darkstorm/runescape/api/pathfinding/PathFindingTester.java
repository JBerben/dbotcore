package org.darkstorm.runescape.api.pathfinding;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Logger;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.Timer;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.api.*;
import org.darkstorm.runescape.api.Menu;
import org.darkstorm.runescape.api.input.*;
import org.darkstorm.runescape.api.pathfinding.astar.*;
import org.darkstorm.runescape.api.pathfinding.astar.GlobalAStarHeuristic.Region;
import org.darkstorm.runescape.api.tab.Tab;
import org.darkstorm.runescape.api.util.*;
import org.darkstorm.runescape.api.wrapper.Player;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.script.*;

public class PathFindingTester extends JPanel implements MouseListener {
	private static final long serialVersionUID = 1L;
	private BufferedImage osrsImage;
	private Point origin, mousePoint;
	private Tile startPath;
	private boolean dragging = false;
	private JFrame frame;
	private Timer timer;

	private AStarPathSearchProvider searchProvider;
	private AStarPathSearch currentSearch;
	private PathNode currentPath, currentDisplay;
	private boolean walk;
	private ExecutorService walkService = Executors.newSingleThreadExecutor();
	private AtomicReference<Future<?>> walkTask = new AtomicReference<>();

	private GameContext context;

	private ExecutorService saveService = Executors.newSingleThreadExecutor();
	private long lastSave = System.currentTimeMillis() + 30000;
	private int lastRegionCount = 0;

	public PathFindingTester() throws IOException {
		this(new TestBotImpl());
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public PathFindingTester(final GameContext context) throws IOException {
		this.context = context;
		searchProvider = new AStarPathSearchProvider(context);
		for(Region region : loadRegions())
			((GlobalAStarHeuristic) searchProvider.getHeuristic())
					.addRegion(region);
		lastRegionCount = ((GlobalAStarHeuristic) searchProvider.getHeuristic()).regions
				.size();
		osrsImage = ImageIO.read(getClass().getResource("/osrs-map.jpg"));
		origin = new Point(osrsImage.getWidth() / 2, osrsImage.getHeight() / 2);
		frame = new JFrame("Canvas");
		setPreferredSize(new Dimension(640, 480));
		setIgnoreRepaint(true);
		frame.add(this);
		frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		requestFocus();

		timer = new Timer(50, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				repaint();
			}
		});
		timer.setRepeats(true);
		timer.setCoalesce(true);
		timer.start();
		addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				mousePoint = e.getPoint();
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if(!dragging) {
					if(e.getButton() == MouseEvent.BUTTON3) {
						if(context instanceof TestBotImpl) {
							Tile tile = screenToTile(e.getPoint());
							((TestBotImpl) context).setBaseTile(new Tile(tile
									.getX() - (tile.getX() % 8), tile.getY()
									- (tile.getY() % 8)));
						} else {
							currentSearch = searchProvider.provideSearch(
									context.getPlayers().getSelf()
											.getLocation(),
									screenToTile(e.getPoint()));
							currentPath = null;
							currentDisplay = null;
							startPath = null;
							walk = true;
							Future<?> future = walkTask.get();
							if(future != null && !future.isDone())
								future.cancel(true);
						}
					} else if(e.getButton() == MouseEvent.BUTTON1) {
						Future<?> future = walkTask.getAndSet(null);
						if(future != null && !future.isDone())
							future.cancel(true);
						walk = false;
						if(startPath != null) {
							currentPath = null;
							currentDisplay = null;
							currentSearch = searchProvider.provideSearch(
									startPath, screenToTile(e.getPoint()));
							startPath = null;
						} else {
							startPath = screenToTile(e.getPoint());
							currentSearch = null;
							currentPath = null;
							currentDisplay = null;
						}
					}
				}
				dragging = false;
			}
		});
		addMouseMotionListener(new MouseMotionAdapter() {
			@Override
			public void mouseDragged(MouseEvent e) {
				dragging = true;
				int dx = e.getX() - mousePoint.x;
				int dy = e.getY() - mousePoint.y;
				origin.setLocation(origin.x - dx, origin.y - dy);
				if(origin.x < 0)
					origin.x = 0;
				else if(origin.x > osrsImage.getWidth() - getWidth())
					origin.x = osrsImage.getWidth() - getWidth();
				if(origin.y < 0)
					origin.y = 0;
				else if(origin.y > osrsImage.getHeight() - getHeight())
					origin.y = osrsImage.getHeight() - getHeight();
				mousePoint = e.getPoint();
			}
		});
		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentHidden(ComponentEvent e) {
				timer.stop();
				osrsImage = null;
				Future<?> task = walkTask.getAndSet(null);
				if(task != null && !task.isDone())
					task.cancel(true);
				walkService.shutdown();
				try {
					walkService.awaitTermination(5, TimeUnit.SECONDS);
				} catch(InterruptedException e2) {}
				currentPath = null;
				currentSearch = null;
				searchProvider = null;
				currentDisplay = null;
				saveService.shutdown();
				try {
					saveService.awaitTermination(10, TimeUnit.SECONDS);
				} catch(InterruptedException e1) {}
				System.gc();
			}
		});
	}

	private Region[] loadRegions() {
		try {
			File data = new File("tiles.dat");
			if(!data.exists())
				return new Region[0];
			List<Region> regions = new ArrayList<>();
			DataInputStream in = new DataInputStream(new FileInputStream(data));
			while(in.readByte() == 0) {
				int rx = in.readInt();
				int ry = in.readInt();
				int len = in.readInt();
				int[][] flags = new int[len][];
				for(int i = 0; i < len; i++) {
					int sublen = in.readInt();
					flags[i] = new int[sublen];
					for(int j = 0; j < sublen; j++)
						flags[i][j] = in.readInt();
				}
				regions.add(new Region(rx, ry, flags));
			}
			in.close();
			return regions.toArray(new Region[regions.size()]);
		} catch(Exception e) {
			e.printStackTrace();
			return new Region[0];
		}
	}

	private void saveRegions(Region[] regions) {
		if(context instanceof TestBotImpl)
			return;
		try {
			File data = new File("tiles.dat");
			if(data.exists()) {
				FileInputStream in = new FileInputStream(data);
				FileOutputStream out = new FileOutputStream(new File(
						data.getName() + ".bak"));
				byte[] buffer = new byte[1024];
				int read;
				while((read = in.read(buffer)) != -1)
					out.write(buffer, 0, read);
				in.close();
				out.close();
			}
			DataOutputStream out = new DataOutputStream(new FileOutputStream(
					data));
			for(Region region : regions) {
				out.writeByte(0);
				out.writeInt(region.x);
				out.writeInt(region.y);
				int[][] flags = region.flags;
				out.writeInt(flags.length);
				for(int[] subflags : flags) {
					out.writeInt(subflags.length);
					for(int flag : subflags)
						out.writeInt(flag);
				}
			}
			out.writeByte(1);
			out.flush();
			out.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	public JFrame getFrame() {
		return frame;
	}

	@Override
	protected void paintComponent(Graphics g) {
		PathNode tempNode = new BasicPathNode(null, new Tile(0, 0));
		searchProvider.getHeuristic().isWalkable(tempNode, tempNode);

		if(System.currentTimeMillis() > lastSave + 15000
				&& (lastRegionCount != ((GlobalAStarHeuristic) searchProvider
						.getHeuristic()).regions.size() || System
						.currentTimeMillis() > lastSave + 60000)) {
			System.out.println("Saving!");
			final Region[] regions = ((GlobalAStarHeuristic) searchProvider
					.getHeuristic()).regions.values().toArray(new Region[0])
					.clone();
			lastRegionCount = regions.length;
			saveService.execute(new Runnable() {
				@Override
				public void run() {
					saveRegions(regions);
				}
			});
			lastSave = System.currentTimeMillis();
		}

		super.paintComponent(g);
		g.drawImage(osrsImage, 0, 0, getWidth(), getHeight(), origin.x,
				origin.y, origin.x + getWidth(), origin.y + getHeight(), null);
		/*Tile[] tiles = new Tile[] { new Tile(2726, 3487), new Tile(2800, 3414),
				new Tile(2655, 3440), new Tile(2635, 3341),
				new Tile(2600, 3296), new Tile(2474, 3435) };
		for(Tile tile : tiles) {
			Point p = tileToScreen(tile);
			g.setColor(Color.BLACK);
			// g.fillOval(p.x - 50, p.y - 50, 100, 100);
			g.setColor(Color.RED);
			g.fillOval(p.x - 2, p.y - 2, 4, 4);
		}*/
		g.setColor(new Color(0, 255, 0, 100));
		if(searchProvider.getHeuristic() instanceof GlobalAStarHeuristic) {
			for(Region region : ((GlobalAStarHeuristic) searchProvider
					.getHeuristic()).regions.values()) {
				int[][] data = region.flags;
				if(data == null)
					continue;
				Tile tile = new Tile(region.x, region.y);
				Point regionOrigin = tileToScreen(tile.offset(0, 8));
				Point regionEnd = tileToScreen(tile.offset(-8, 8 * 2));
				g.fillRect(regionOrigin.x, regionOrigin.y, regionOrigin.x
						- regionEnd.x, regionOrigin.y - regionEnd.y);
			}
		}
		Tile region = new Tile(context.getGame().getRegionBaseX(), context
				.getGame().getRegionBaseY());
		Point regionOrigin = tileToScreen(region.offset(0, 104));
		Point regionEnd = tileToScreen(region.offset(-104, 104 * 2));
		g.fillRect(regionOrigin.x, regionOrigin.y,
				regionOrigin.x - regionEnd.x, regionOrigin.y - regionEnd.y);

		if(startPath != null) {
			g.setColor(Color.MAGENTA);
			Point screen = tileToScreen(startPath);
			g.fillRect(screen.x - 1, screen.y - 1, 2, 2);
		}

		if(currentSearch != null) {
			long end = System.currentTimeMillis() + 50;
			while(!currentSearch.isDone() && System.currentTimeMillis() < end)
				currentSearch.step();
			if(currentPath == null && currentSearch.isDone()) {
				currentPath = currentSearch.getPath();
				if(currentPath == null)
					currentSearch = null;
			}
		}
		if(currentSearch != null) {
			for(PathNode node : currentSearch.getNodeWorld()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(new Color(255, 255, 0, 255));
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
			for(PathNode node : currentSearch.getOpenSet()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(Color.GREEN);
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
			for(PathNode node : currentSearch.getClosedSet()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(Color.BLUE);
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
			for(PathNode node : currentSearch.getNodeWorldReverse()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(new Color(255, 255, 0, 100));
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
			for(PathNode node : currentSearch.getOpenSetReverse()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(new Color(0, 255, 0, 100));
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
			for(PathNode node : currentSearch.getClosedSetReverse()) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(new Color(0, 0, 255, 100));
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
			}
		}
		if(currentPath != null) {
			PathNode node = currentPath;
			while(node != null) {
				Point p = tileToScreen(node.getLocation());
				g.setColor(Color.RED);
				g.fillRect(p.x - 1, p.y - 1, 2, 2);
				node = node.getNext();
			}
			if(currentDisplay == null)
				currentDisplay = currentPath;
			Point p = tileToScreen(currentDisplay.getLocation());
			g.setColor(Color.MAGENTA);
			g.fillRect(p.x - 2, p.y - 2, 4, 4);
			currentDisplay = currentDisplay.getNext();

			if(walk) {
				walk = false;
				final PathNode path = currentPath;
				Future<?> future = walkTask.getAndSet(walkService
						.submit(new Runnable() {
							@Override
							public void run() {
								context.getWalking().walkPath(
										new GeneratedTilePath(context, path));
							}
						}));
				if(future != null && !future.isDone())
					future.cancel(true);
			}
		}

		g.setFont(g.getFont().deriveFont(Font.BOLD));
		FontMetrics metrics = g.getFontMetrics();
		Players players = context.getPlayers();
		if(players != null) {
			Player player = players.getSelf();
			Tile location = player.getLocation();
			Point screen = tileToScreen(location);
			g.setColor(Color.WHITE);
			g.fillRect(screen.x - 1, screen.y - 1, 2, 2);
			String message = "Self";
			g.setColor(Color.BLACK);
			g.drawString(message, screen.x + 6, screen.y + 6);
			g.setColor(Color.WHITE);
			g.drawString(message, screen.x + 5, screen.y + 5);
		}

		Point mouse = getMousePosition();
		if(mouse == null)
			return;
		String message = "Mouse location: " + mouse.x + ", " + mouse.y;
		g.setColor(Color.BLACK);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 4,
				getHeight() - 14 - metrics.getHeight() * 2);
		g.setColor(Color.RED);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 5,
				getHeight() - 15 - metrics.getHeight() * 2);
		message = "Map location: " + (origin.x + mouse.x) + ", "
				+ (origin.y + mouse.y);
		g.setColor(Color.BLACK);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 4,
				getHeight() - 9 - metrics.getHeight());
		g.setColor(Color.RED);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 5,
				getHeight() - 10 - metrics.getHeight());
		message = "World location: " + screenToTile(mouse).getX() + ", "
				+ screenToTile(mouse).getY();
		g.setColor(Color.BLACK);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 4,
				getHeight() - 4);
		g.setColor(Color.RED);
		g.drawString(message, getWidth() - metrics.stringWidth(message) - 5,
				getHeight() - 5);
	}

	private Point tileToScreen(Tile tile) {
		int x = 128 + (int) ((tile.getX() - 2048) / 0.5);
		int y = osrsImage.getHeight()
				- (int) (128 + (tile.getY() - 2495) / 0.5);
		return new Point(x - origin.x, y - origin.y);
	}

	private Tile screenToTile(Point point) {
		int x = (int) (((point.x + origin.x) - 128) * 0.5) + 2048;
		int y = (int) (((osrsImage.getHeight() - (point.y + origin.y)) - 128) * 0.5) + 2495;
		return new Tile(x, y);
	}

	public static void main(String[] args) throws Exception {
		new PathFindingTester();
	}

	private static class TestBotImpl implements Bot, GameContext, Calculations {
		private TestGameImpl game = new TestGameImpl();
		private Tile baseTile = new Tile(0, 0);
		private EventManager eventManager = new BasicEventManager();
		private int[][] dataArray = new int[104][104];

		private void setBaseTile(Tile baseTile) {
			this.baseTile = baseTile;
			dataArray = new int[104][104];
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
		public Point getLimitlessWorldScreenLocation(double x, double y,
				int height) {
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
		public TestGameImpl getGame() {
			return game;
		}

		@Override
		public Canvas getCanvas() {
			return null;
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

		private class TestGameImpl extends Applet implements Game {
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
				return TestBotImpl.this;
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
				return baseTile.getX();
			}

			@Override
			public int getRegionBaseY() {
				return baseTile.getY();
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
				return dataArray;
			}
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
}
