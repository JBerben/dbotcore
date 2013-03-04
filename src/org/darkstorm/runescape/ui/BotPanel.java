package org.darkstorm.runescape.ui;

import java.awt.*;

import javax.swing.*;

import org.darkstorm.runescape.Bot;

@SuppressWarnings("serial")
public class BotPanel extends JPanel {
	private final Bot bot;
	private final Component display;
	private final LogPane log;

	public BotPanel(Bot bot) {
		this.bot = bot;

		setLayout(new BorderLayout());
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		display = bot.getDisplay();
		splitPane.setTopComponent(display);
		log = new LogPane(bot.getLogger());
		splitPane.setBottomComponent(log);
		splitPane.setResizeWeight(0.8);
		add(splitPane, BorderLayout.CENTER);
	}

	public Bot getBot() {
		return bot;
	}
}
