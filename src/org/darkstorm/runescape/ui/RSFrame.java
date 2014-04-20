package org.darkstorm.runescape.ui;

import java.awt.BorderLayout;
import java.beans.*;

import javax.swing.*;

import org.darkstorm.runescape.*;

@SuppressWarnings("serial")
public class RSFrame extends JFrame implements PropertyChangeListener {
	private final DarkBotRS darkbot;
	private final BotContainer container;

	public RSFrame(DarkBotRS darkbot) {
		super("DarkBot");
		this.darkbot = darkbot;
		container = new BotContainer(this);
		setLayout(new BorderLayout());
		add(container, BorderLayout.CENTER);
		container.addTab("Test1", new JPanel());
		container.addTab("Test2", new JPanel());
		container.addPropertyChangeListener(this);

		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setSize(790, 700);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if(container.equals(event.getSource())
				&& event.getPropertyName().equals("TAB_ADD")) {
			int index = JOptionPane
					.showOptionDialog(this, "Choose a bot type", "New Bot",
							JOptionPane.OK_CANCEL_OPTION,
							JOptionPane.QUESTION_MESSAGE, null,
							GameType.values(), null);
			if(index < 0)
				return;
			GameType type = GameType.values()[index];
			Bot bot = darkbot.createBot(type);
			container.addTab("Bot[" + bot.getName() + "]", new BotPanel(bot));
		}
	}
}
