package org.darkstorm.runescape.ui;

import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.event.*;

import org.darkstorm.runescape.*;
import org.darkstorm.runescape.script.*;

public class BotContainer extends JTabbedPane {
	private static final long serialVersionUID = -5923476382802695732L;
	private final RSFrame frame;
	private final TabControlRenderer renderer = new TabControlRenderer();

	private final TabButton play, pause, stop, input, settings;
	private final TabButton[] buttons;

	private final JPopupMenu inputMenu;

	public BotContainer(RSFrame frame) {
		this.frame = frame;
		play = new TabButton("/play.png", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Bot bot = getCurrentBot();
				if(bot != null)
					new ScriptLoaderFrame(bot);
				updateButtonStates();
			}
		});
		play.setEnabled(false);
		pause = new TabButton("/pause.png", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Bot bot = getCurrentBot();
				if(bot != null)
					for(Script script : bot.getScriptManager()
							.getActiveScripts())
						if(script.isTopLevel())
							if(!script.isPaused())
								script.pause();
							else
								script.resume();
				updateButtonStates();
			}
		});
		pause.setEnabled(false);
		stop = new TabButton("/stop.png", new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Bot bot = getCurrentBot();
				if(bot != null) {
					for(Script script : bot.getScriptManager()
							.getActiveScripts())
						if(script.isTopLevel())
							script.stop();
				}
				updateButtonStates();
			}
		});
		stop.setEnabled(false);
		input = new TabButton("/keyboard.png", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle inputBounds = input.getBounds();
				inputMenu.show(BotContainer.this, inputBounds.x, inputBounds.y
						+ inputBounds.height);
				updateButtonStates();
			}
		});
		input.setEnabled(false);
		settings = new TabButton("/settings.png", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Rectangle settingBounds = settings.getBounds();
				BotPanel panel = getCurrentBotPanel();
				if(panel == null)
					return;
				panel.update();
				panel.getSettingsMenu()
						.show(BotContainer.this, settingBounds.x,
								settingBounds.y + settingBounds.height);
				updateButtonStates();
			}
		});

		inputMenu = new JPopupMenu();
		ItemListener inputItemListener = new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(e.getStateChange() == ItemEvent.SELECTED) {
					Bot bot = getCurrentBot();
					if(bot == null)
						return;
					JRadioButtonMenuItem item = (JRadioButtonMenuItem) e
							.getSource();
					if(item.getText().equals("None"))
						bot.setInputState(InputState.NONE);
					else if(item.getText().equals("Keyboard"))
						bot.setInputState(InputState.KEYBOARD);
					else if(item.getText().equals("Mouse/Keyboard"))
						bot.setInputState(InputState.MOUSE_KEYBOARD);
					updateButtonStates();
				}
			}
		};
		ButtonGroup group = new ButtonGroup();
		JMenuItem item = new JRadioButtonMenuItem("None");
		item.addItemListener(inputItemListener);
		group.add(item);
		inputMenu.add(item);
		item = new JRadioButtonMenuItem("Keyboard");
		item.addItemListener(inputItemListener);
		group.add(item);
		inputMenu.add(item);
		item = new JRadioButtonMenuItem("Mouse/Keyboard", true);
		item.addItemListener(inputItemListener);
		group.add(item);
		inputMenu.add(item);
		inputMenu.pack();

		buttons = new TabButton[] { play, pause, stop, input, settings };

		Timer timer = new Timer(200, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateButtonStates();
			}
		});
		timer.setRepeats(true);
		timer.start();

		addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				updateButtonStates();
			}
		});
	}

	private void updateButtonStates() {
		Bot bot = getCurrentBot();
		if(bot != null) {
			play.setEnabled(bot.canPlayScript());
			ScriptManager manager = bot.getScriptManager();
			boolean active = manager.getActiveScripts().length > 0;
			pause.setEnabled(active);
			stop.setEnabled(active);
			input.setEnabled(true);

			switch(bot.getInputState()) {
			case NONE:
				((JRadioButtonMenuItem) inputMenu.getComponent(0))
						.setSelected(true);
				((JRadioButtonMenuItem) inputMenu.getComponent(1))
						.setSelected(false);
				((JRadioButtonMenuItem) inputMenu.getComponent(2))
						.setSelected(false);
				break;
			case KEYBOARD:
				((JRadioButtonMenuItem) inputMenu.getComponent(0))
						.setSelected(false);
				((JRadioButtonMenuItem) inputMenu.getComponent(1))
						.setSelected(true);
				((JRadioButtonMenuItem) inputMenu.getComponent(2))
						.setSelected(false);
				break;
			case MOUSE_KEYBOARD:
				((JRadioButtonMenuItem) inputMenu.getComponent(0))
						.setSelected(false);
				((JRadioButtonMenuItem) inputMenu.getComponent(1))
						.setSelected(false);
				((JRadioButtonMenuItem) inputMenu.getComponent(2))
						.setSelected(true);
				break;
			}

			BotPanel panel = getCurrentBotPanel();
			if(panel != null)
				panel.update();
		} else {
			play.setEnabled(false);
			pause.setEnabled(false);
			stop.setEnabled(false);
			input.setEnabled(false);
		}
	}

	private Bot getCurrentBot() {
		Component component = getSelectedComponent();
		if(component instanceof BotPanel)
			return ((BotPanel) component).getBot();
		return null;
	}

	private BotPanel getCurrentBotPanel() {
		Component component = getSelectedComponent();
		if(component instanceof BotPanel)
			return (BotPanel) component;
		return null;
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		renderer.paint(g);
	}

	@Override
	public void addTab(String title, Component component) {
		super.addTab(title, component);
		updateTabs();
	}

	@Override
	public void addTab(String title, Icon icon, Component component) {
		super.addTab(title, icon, component);
		updateTabs();
	}

	@Override
	public void addTab(String title, Icon icon, Component component, String tip) {
		super.addTab(title, icon, component, tip);
		updateTabs();
	}

	private void updateTabs() {
		for(int i = 0; i < getTabCount(); i++) {
			JLabel label = (JLabel) getTabComponentAt(i);
			if(label == null) {
				label = new JLabel(getTitleAt(i));
				setTabComponentAt(i, label);
			}
			Dimension preferredSize = label.getUI().getPreferredSize(label);
			label.setPreferredSize(new Dimension(preferredSize.width + 20,
					preferredSize.height));
		}
	}

	private class TabControlRenderer implements MouseListener,
			MouseMotionListener {
		private boolean pressed = false;
		private int mouseX = 0, mouseY = 0;
		private final int width = 6, height = 6;

		public TabControlRenderer() {
			addMouseMotionListener(this);
			addMouseListener(this);
		}

		@Override
		public void mouseEntered(MouseEvent me) {
			updateCursor();
			repaint();
		}

		@Override
		public void mouseExited(MouseEvent me) {
			updateCursor();
			repaint();
		}

		@Override
		public void mousePressed(MouseEvent me) {
			if(me.getButton() == MouseEvent.BUTTON1)
				pressed = true;
			updateCursor();
			repaint();
		}

		@Override
		public void mouseClicked(MouseEvent me) {
		}

		@Override
		public void mouseDragged(MouseEvent me) {
		}

		@Override
		public void mouseReleased(MouseEvent me) {
			if(me.getButton() == MouseEvent.BUTTON1)
				pressed = false;
			int selectedTab = getSelectedTab(me.getX(), me.getY());
			if(selectedTab != -1
					&& isOverClose(selectedTab, me.getX(), me.getY())) {
				notifyTabClose(selectedTab);
				if(getSelectedIndex() > 0)
					setSelectedIndex(getSelectedIndex() - 1);
				removeTabAt(selectedTab);
			} else if(isOverAdd(me.getX(), me.getY()))
				notifyTabAdd();
			else
				for(TabButton button : buttons)
					if(isOverButton(button, me.getX(), me.getY()))
						button.press();
			updateCursor();
			repaint();
		}

		@Override
		public void mouseMoved(MouseEvent me) {
			mouseX = me.getX();
			mouseY = me.getY();
			updateCursor();
			repaint();
		}

		private void updateCursor() {
			if(getTabCount() > 0) {
				int selectedTab = getSelectedTab(mouseX, mouseY);
				if(selectedTab != -1
						&& isOverClose(selectedTab, mouseX, mouseY)) {
					// setCursor(new Cursor(Cursor.HAND_CURSOR));
					setToolTipTextAt(selectedTab, "Close tab");
				} else if(isOverAdd(mouseX, mouseY)) {
					// setCursor(new Cursor(Cursor.HAND_CURSOR));
					setToolTipText("New tab");
				} else {
					// setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
					for(int i = 0; i < getTabCount(); i++)
						setToolTipTextAt(i, null);
					setToolTipText(null);
				}
			}
		}

		private int getSelectedTab(int x, int y) {
			int tabCount = getTabCount();
			for(int j = 0; j < tabCount; j++)
				if(getBoundsAt(j).contains(mouseX, mouseY))
					return j;
			return -1;
		}

		private boolean isOverClose(int tab, int x, int y) {
			Rectangle bounds = getCloseBounds(tab);
			return x >= bounds.x && x <= bounds.x + bounds.width
					&& y >= bounds.y && y <= bounds.y + bounds.height;
		}

		private boolean isOverAdd(int x, int y) {
			Rectangle bounds = getAddBounds();
			return x >= bounds.x && x <= bounds.x + bounds.width
					&& y >= bounds.y && y <= bounds.y + bounds.height;
		}

		private boolean isOverButton(TabButton button, int x, int y) {
			resizeButtons();
			Rectangle bounds = button.getBounds();
			return bounds.contains(x, y);
		}

		public void paint(Graphics g) {
			int tabCount = getTabCount();
			for(int j = 0; j < tabCount; j++) {
				Rectangle bounds = getCloseBounds(j);
				drawClose((Graphics2D) g, bounds.x, bounds.y);
			}

			Rectangle bounds = getAddBounds();
			drawAdd((Graphics2D) g, bounds.x, bounds.y);

			resizeButtons();
			for(TabButton button : buttons) {
				boolean over = isOverButton(button, mouseX, mouseY);
				button.paint(g, over, over && pressed);
			}
		}

		private void resizeButtons() {
			int tabCount = getTabCount();
			int offset = 4;
			for(int i = buttons.length - 1; i >= 0; i--) {
				Rectangle bounds = buttons[i].getBounds();
				bounds.x = getWidth() - offset - bounds.width;
				offset += bounds.width + 8;
				if(tabCount > 0) {
					Rectangle tabBounds = getBoundsAt(getSelectedIndex());
					bounds.y = tabBounds.y + tabBounds.height / 2
							- bounds.height / 2;
				} else
					bounds.y = 4;
			}
		}

		private void drawAdd(Graphics2D g2, int x, int y) {
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
					RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
					RenderingHints.VALUE_INTERPOLATION_BILINEAR);
			g2.setStroke(new BasicStroke(5, BasicStroke.JOIN_ROUND,
					BasicStroke.CAP_ROUND));
			g2.setColor(Color.BLACK);
			g2.drawLine(x, y + height / 2, x + width, y + height / 2);
			g2.drawLine(x + width / 2, y, x + width / 2, y + height);
			g2.setColor(isUnderMouse(x, y) ? pressed ? Color.GREEN : new Color(
					100, 255, 100) : Color.WHITE);
			g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND,
					BasicStroke.CAP_ROUND));
			g2.drawLine(x, y + height / 2, x + width, y + height / 2);
			g2.drawLine(x + width / 2, y, x + width / 2, y + height);
		}

		private void drawClose(Graphics2D g2, int x, int y) {
			if(getTabCount() > 0) {
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
						RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
						RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				g2.setStroke(new BasicStroke(5, BasicStroke.JOIN_ROUND,
						BasicStroke.CAP_ROUND));
				g2.setColor(Color.BLACK);
				g2.drawLine(x, y, x + width, y + height);
				g2.drawLine(x + width, y, x, y + height);
				g2.setColor(isUnderMouse(x, y) ? pressed ? Color.RED
						: new Color(255, 100, 100) : Color.WHITE);
				g2.setStroke(new BasicStroke(3, BasicStroke.JOIN_ROUND,
						BasicStroke.CAP_ROUND));
				g2.drawLine(x, y, x + width, y + height);
				g2.drawLine(x + width, y, x, y + height);
			}
		}

		private boolean isUnderMouse(int x, int y) {
			return mouseX - x >= 0 && mouseX - x - 2 <= width
					&& mouseY - y >= 0 && mouseY - y - 2 <= height;
		}

		private Rectangle getCloseBounds(int tab) {
			Rectangle bounds = getBoundsAt(tab);
			Rectangle componentBounds = getTabComponentAt(tab) != null ? getTabComponentAt(
					tab).getBounds()
					: bounds;

			int closeX = bounds.x + bounds.width - width - 8, closeY = componentBounds.y
					+ componentBounds.height / 2 - height / 2;
			return new Rectangle(closeX, closeY, width + 2, height + 2);
		}

		private Rectangle getAddBounds() {
			int x, y;
			int tabCount = getTabCount();
			if(tabCount > 0) {
				Rectangle bounds = getBoundsAt(tabCount - 1);
				Rectangle componentBounds = getTabComponentAt(getSelectedIndex()) != null ? getTabComponentAt(
						getSelectedIndex()).getBounds()
						: bounds;
				x = bounds.x + bounds.width + 6;
				y = componentBounds.y + componentBounds.height / 2 - height / 2;
			} else
				x = y = 6;
			return new Rectangle(x, y, width + 2, height + 2);
		}
	}

	public void notifyTabAdd() {
		firePropertyChange("TAB_ADD", 0, 1);
	}

	public void notifyTabClose(int tabIndex) {
		Component c = getComponentAt(tabIndex);
		c.firePropertyChange("TAB_REMOVE", 0, 1);
	}

	public RSFrame getFrame() {
		return frame;
	}

	private static class TabButton {
		private final Image icon, disabledIcon, hoveringIcon, pressedIcon;
		private final Rectangle bounds;
		private final ActionListener listener;

		private boolean enabled = true;

		public TabButton(String iconPath, ActionListener listener) {
			BufferedImage icon;
			try {
				icon = ImageIO.read(getClass().getResourceAsStream(iconPath));
			} catch(IOException exception) {
				icon = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
			}
			this.icon = icon;

			ColorSpace colorSpace = ColorSpace.getInstance(ColorSpace.CS_GRAY);
			ColorConvertOp convertOp = new ColorConvertOp(colorSpace, null);
			RescaleOp rescaleOp = new RescaleOp(new float[] { 0.5f, 0.5f, 0.5f,
					1.0f }, new float[4], null);
			disabledIcon = convertOp.filter(rescaleOp.filter(icon, null), null);

			rescaleOp = new RescaleOp(new float[] { 1.2f, 1.2f, 1.2f, 1.0f },
					new float[4], null);
			hoveringIcon = rescaleOp.filter(icon, null);

			rescaleOp = new RescaleOp(new float[] { 0.7f, 0.7f, 0.7f, 1.0f },
					new float[4], null);
			pressedIcon = rescaleOp.filter(icon, null);

			this.listener = listener;
			bounds = new Rectangle(0, 0, icon.getWidth(), icon.getHeight());
		}

		public void paint(Graphics g, boolean hovering, boolean pressed) {
			Image image = icon;
			if(!enabled)
				image = disabledIcon;
			else if(pressed)
				image = pressedIcon;
			else if(hovering)
				image = hoveringIcon;
			g.drawImage(image,
					bounds.x + (bounds.width / 2 - image.getWidth(null) / 2),
					bounds.y + (bounds.height / 2 - image.getHeight(null) / 2),
					null);
		}

		public Rectangle getBounds() {
			return bounds;
		}

		public void press() {
			if(listener != null && enabled)
				listener.actionPerformed(new ActionEvent(this, 0, "press"));
		}

		@SuppressWarnings("unused")
		public boolean isEnabled() {
			return enabled;
		}

		public void setEnabled(boolean enabled) {
			this.enabled = enabled;
		}
	}
}