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
				Loader loader = new OldSchoolLoader(OldSchoolBot.this,
				/*new Random().nextInt(80)*/78);
				try {
					Cache cache = /*new EmptyCache();*/new DirectoryCache(
							new File("cache"));
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
		g.drawString("Paint test.", 5, 40);
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
