package org.darkstorm.runescape;

import java.awt.Frame;
import java.text.DateFormat;
import java.util.Date;
import java.util.logging.*;

import javax.swing.UIManager;

import org.darkstorm.runescape.oldschool.OldSchoolBot;
import org.darkstorm.runescape.ui.RSFrame;

public final class DarkBotRS extends AbstractDarkBot {
	private final RSFrame frame;

	public DarkBotRS() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		configureLogging();

		frame = new RSFrame(this);
	}

	private void configureLogging() {
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

	@Override
	public Bot createBot(GameType type) {
		Bot bot;
		switch(type) {
		case OLDSCHOOL:
			bot = new OldSchoolBot(this);
			break;
		default:
			return null;
		}
		addBot(bot);
		return bot;
	}

	@Override
	public Frame getFrame() {
		return frame;
	}

	public static void main(String[] args) {
		new DarkBotRS();
	}
}
