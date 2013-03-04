package org.darkstorm.runescape;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.*;

import java.text.DateFormat;

import javax.swing.UIManager;

import org.darkstorm.runescape.event.EventManager;
import org.darkstorm.runescape.oldschool.OldSchoolBot;
import org.darkstorm.runescape.ui.RSFrame;

public final class DarkBotRS {
	private final RSFrame ui;
	private final List<Bot> bots;
	private final EventManager eventManager;

	static {
		Logger logger = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME).getParent();
		for(Handler handler : logger.getHandlers())
			logger.removeHandler(handler);
		logger.setLevel(Level.FINER);
		logger.addHandler(new Handler() {
			DateFormat format = DateFormat.getTimeInstance(DateFormat.MEDIUM);

			@Override
			public void publish(LogRecord record) {
				if(!record.getSourceClassName().startsWith("org.darkstorm"))
					return;
				String message = format.format(new Date(record.getMillis()))
						+ " [" + record.getLoggerName() + "] "
						+ record.getLevel().getName() + ": ";
				if(record.getThrown() != null) {
					System.err.println(message);
					record.getThrown().printStackTrace(System.err);
				} else if(record.getLevel() == Level.SEVERE)
					System.err.println(message + record.getMessage());
				else
					System.out.println(message + record.getMessage());
			}

			@Override
			public void flush() {
			}

			@Override
			public void close() throws SecurityException {
			}
		});
	}

	public DarkBotRS() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception exception) {
			exception.printStackTrace();
		}

		eventManager = new EventManager();
		ui = new RSFrame(this);
		bots = new CopyOnWriteArrayList<Bot>();
	}

	public Bot createBot(GameType type) {
		Bot bot;
		switch(type) {
		case OLDSCHOOL:
			bot = new OldSchoolBot(this);
			break;
		default:
			return null;
		}
		bots.add(bot);
		return bot;
	}

	public RSFrame getUI() {
		return ui;
	}

	public EventManager getEventManager() {
		return eventManager;
	}

	public static void main(String[] args) {
		new DarkBotRS();
	}
}
