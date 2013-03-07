package org.darkstorm.runescape.oldschool;

import java.util.logging.Logger;

import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.swing.JPanel;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.util.*;

import com.cherokee.utils.ReflectionBuddy;

public class OldSchoolBot implements Bot, EventListener {
	private final DarkBotRS darkbot;
	private final Logger logger;
	private final EventManager eventManager;

	private Applet game;
	private BotCanvas canvas;
	private JPanel display;
	private BufferedImage gameImage, botImage;

	public OldSchoolBot(DarkBotRS darkbot) {
		this.darkbot = darkbot;
		logger = Logger.getLogger("OldSchoolBot");
		eventManager = new EventManager();

		gameImage = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);
		botImage = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);

		final ImageStatus status = new ImageStatus(botImage);
		canvas = new BotCanvas(this);
		display = new JPanel(new FlowLayout());
		display.add(canvas);

		eventManager.registerListener(this);

		new Thread(new Runnable() {

			@Override
			public void run() {
				status.setProgressShown(true);
				status.setMessage("Loading...");
				status.setProgressShown(true);
				status.setProgress(0);
				Cache cache = new DirectoryCache(new File("cache"));
				int world = 1 + (int) (Math.random() * 78);
				if(cache.isCached("world")) {
					try {
						world = Integer.parseInt(new String(cache
								.loadCache("world")).replace("\n", "").trim());
					} catch(NumberFormatException exception) {
						exception.printStackTrace();
					}
				}
				cache.saveCache("world", Integer.toString(world).getBytes());
				Loader loader = new OldSchoolLoader(OldSchoolBot.this, world);
				try {
					loader.load(cache, status);
					game = loader.createApplet(cache, status);
					System.out.println(game.getClass().getField("bot")
							.get(null));
					new ReflectionBuddy(game).setVisible(true);
				} catch(Exception exception) {
					exception.printStackTrace();
					System.exit(1);
				}
			}
		}).start();
	}

	@EventHandler
	public void onCallback(CallbackEvent event) {
		if(event.getCallback().equals("paint")) {
			gameImage.flush();
			event.setReturnObject(gameImage.getGraphics());
		}
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		Graphics g = event.getGraphics();
		g.setColor(Color.YELLOW);
		String location = "Unknown";
		try {
			Object[] players = (Object[]) game.getClass()
					.getMethod("getPlayers", new Class<?>[0]).invoke(game);
			Object player = players[players.length - 1];
			double x = (Integer) player.getClass().getSuperclass()
					.getMethod("getX", new Class<?>[0]).invoke(player);
			double y = (Integer) player.getClass().getSuperclass()
					.getMethod("getY", new Class<?>[0]).invoke(player);
			x /= 128D;
			y /= 128D;
			location = "(" + x + ", " + y + ")";
		} catch(Exception exception) {}
		g.drawString("Your location: " + location, 5, 40);
	}

	@Override
	public String getName() {
		return "OldSchool";
	}

	@Override
	public Component getDisplay() {
		return display;
	}

	@Override
	public Applet getGame() {
		return game;
	}

	BufferedImage getBotImage() {
		return botImage;
	}

	BufferedImage getGameImage() {
		return gameImage;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public GameContext getGameContext() {
		return null;
	}

	@Override
	public DarkBotRS getDarkBot() {
		return darkbot;
	}
}
