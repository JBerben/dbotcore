package org.darkstorm.runescape.ui;

import java.awt.*;
import java.awt.event.*;
import java.util.*;

import javax.swing.*;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.event.*;
import org.darkstorm.runescape.event.EventListener;
import org.darkstorm.runescape.event.game.PaintEvent;
import org.darkstorm.runescape.ui.debug.*;

@SuppressWarnings("serial")
public class BotPanel extends JPanel implements EventListener {
	private final Bot bot;
	private final Component display;
	private final LogPane log;

	private final Map<Debug, JCheckBoxMenuItem> debugs;
	private final JPopupMenu settingsMenu;

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

		debugs = new HashMap<Debug, JCheckBoxMenuItem>();
		debugs.put(new PlayerDebug(bot), new JCheckBoxMenuItem("Players"));
		debugs.put(new NPCDebug(bot), new JCheckBoxMenuItem("NPCs"));
		debugs.put(new GroundItemDebug(bot),
				new JCheckBoxMenuItem("GroundItem"));
		debugs.put(new LocationDebug(bot), new JCheckBoxMenuItem("Location"));
		debugs.put(new MenuDebug(bot), new JCheckBoxMenuItem("Menu"));
		debugs.put(new GameObjectDebug(bot), new JCheckBoxMenuItem("Objects"));

		settingsMenu = new JPopupMenu();
		for(final Debug debug : debugs.keySet()) {
			final JCheckBoxMenuItem menuItem = debugs.get(debug);
			settingsMenu.add(menuItem);
			menuItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					if(debug.isActive())
						debug.stop();
					else
						debug.start();
					menuItem.setSelected(debug.isActive());
				}
			});
		}
		settingsMenu.pack();

		bot.getEventManager().registerListener(this);
	}

	public Bot getBot() {
		return bot;
	}

	public JPopupMenu getSettingsMenu() {
		return settingsMenu;
	}

	public void update() {
		for(Debug debug : debugs.keySet()) {
			JCheckBoxMenuItem menuItem = debugs.get(debug);
			menuItem.setSelected(debug.isActive());
		}
	}

	@EventHandler
	public void onPaint(PaintEvent event) {
		Graphics g = event.getGraphics();
		g.setColor(Color.YELLOW);
		int x = 5, y = 40;
		int offsetHeight = g.getFontMetrics().getHeight() + 5;
		for(Debug debug : debugs.keySet()) {
			if(!debug.isActive())
				continue;
			String string = debug.getDisplayedText();
			if(string == null)
				continue;
			g.drawString(string, x, y);
			y += offsetHeight;
		}
	}
}
