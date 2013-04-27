package org.darkstorm.runescape.oldschool;

import java.applet.Applet;
import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.logging.Logger;

import javax.swing.JPanel;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.api.GameContext;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.internal.CallbackEvent;
import org.darkstorm.runescape.script.*;
import org.darkstorm.runescape.util.*;

public class OldSchoolBot implements Bot, EventListener {
	private final DarkBot darkbot;
	private final Logger logger;
	private final EventManager eventManager;
	private final ScriptManager scriptManager;
	private final RandomEventManager randomEventManager;

	private Applet game;
	private BotCanvas canvas;
	private JPanel display;
	private BufferedImage gameImage;// , botImage;
	private GameContext context;
	private InputState inputState = InputState.MOUSE_KEYBOARD;

	public OldSchoolBot(DarkBot darkbot) {
		this.darkbot = darkbot;
		logger = Logger.getLogger("OldSchoolBot");
		eventManager = new BasicEventManager();
		scriptManager = new ScriptManagerImpl(this);
		randomEventManager = new RandomEventManagerImpl(this);

		gameImage = new BufferedImage(765, 503, BufferedImage.TYPE_INT_RGB);
		// botImage = new BufferedImage(765, 503, BufferedImage.TYPE_INT_ARGB);

		final ImageStatus status = new ImageStatus(gameImage);
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
				int world = 2;// 1 + (int) (Math.random() * 78);
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
					// context = loader.createContext();
					// new ReflectionBuddy(game).setVisible(true);
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

	@Override
	public InputState getInputState() {
		return inputState;
	}

	@Override
	public boolean canPlayScript() {
		return context != null;
	}

	@Override
	public void setInputState(InputState state) {
		inputState = state;
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

	@Override
	public Canvas getCanvas() {
		return canvas;
	}

	// BufferedImage getBotImage() {
	// return botImage;
	// }

	BufferedImage getGameImage() {
		return gameImage;
	}

	@Override
	public Logger getLogger() {
		return logger;
	}

	@Override
	public void dispatchInputEvent(InputEvent event) {
		canvas.handleEvent(event);
	}

	@Override
	public EventManager getEventManager() {
		return eventManager;
	}

	@Override
	public GameContext getGameContext() {
		return context;
	}

	@Override
	public ScriptManager getScriptManager() {
		return scriptManager;
	}

	@Override
	public RandomEventManager getRandomEventManager() {
		return randomEventManager;
	}

	@Override
	public DarkBot getDarkBot() {
		return darkbot;
	}
}
