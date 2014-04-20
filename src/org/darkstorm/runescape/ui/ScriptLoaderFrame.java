package org.darkstorm.runescape.ui;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.EtchedBorder;
import javax.swing.filechooser.FileFilter;

import org.darkstorm.runescape.Bot;
import org.darkstorm.runescape.script.*;

@SuppressWarnings("serial")
public class ScriptLoaderFrame extends JDialog {
	private static final Map<ScriptCategory, Icon> categoryIcons;
	private static File selectedFile = new File("../DarkBot-Scripts");

	private JTextField searchField;
	private JPanel scriptPane;
	private ScriptManager manager;

	static {
		Map<ScriptCategory, Icon> icons = new HashMap<ScriptCategory, Icon>();
		try {
			BufferedImage image = ImageIO.read(ScriptLoaderFrame.class
					.getResource("/scripts.png"));
			icons.put(ScriptCategory.AGILITY,
					new ImageIcon(image.getSubimage(0, 0, 32, 32)));
			icons.put(ScriptCategory.OTHER,
					new ImageIcon(image.getSubimage(0, 32, 32, 32)));
			icons.put(ScriptCategory.COMBAT,
					new ImageIcon(image.getSubimage(0, 64, 32, 32)));
			icons.put(ScriptCategory.CONSTRUCTION,
					new ImageIcon(image.getSubimage(0, 96, 32, 32)));
			icons.put(ScriptCategory.COOKING,
					new ImageIcon(image.getSubimage(0, 128, 32, 32)));
			icons.put(ScriptCategory.CRAFTING,
					new ImageIcon(image.getSubimage(0, 160, 32, 32)));
			icons.put(ScriptCategory.DUNGEONEERING,
					new ImageIcon(image.getSubimage(0, 192, 32, 32)));
			icons.put(ScriptCategory.FARMING,
					new ImageIcon(image.getSubimage(0, 224, 32, 32)));
			icons.put(ScriptCategory.FIREMAKING,
					new ImageIcon(image.getSubimage(0, 256, 32, 32)));
			icons.put(ScriptCategory.FISHING,
					new ImageIcon(image.getSubimage(0, 288, 32, 32)));
			icons.put(ScriptCategory.FLETCHING,
					new ImageIcon(image.getSubimage(0, 320, 32, 32)));
			icons.put(ScriptCategory.HERBLORE,
					new ImageIcon(image.getSubimage(0, 352, 32, 32)));
			icons.put(ScriptCategory.HUNTER,
					new ImageIcon(image.getSubimage(0, 384, 32, 32)));
			icons.put(ScriptCategory.MAGIC,
					new ImageIcon(image.getSubimage(0, 416, 32, 32)));
			icons.put(ScriptCategory.MINIGAME,
					new ImageIcon(image.getSubimage(0, 448, 32, 32)));
			icons.put(ScriptCategory.MINING,
					new ImageIcon(image.getSubimage(0, 480, 32, 32)));
			icons.put(ScriptCategory.MONEY,
					new ImageIcon(image.getSubimage(0, 512, 32, 32)));
			icons.put(ScriptCategory.PRAYER,
					new ImageIcon(image.getSubimage(0, 544, 32, 32)));
			icons.put(ScriptCategory.QUEST,
					new ImageIcon(image.getSubimage(0, 576, 32, 32)));
			icons.put(ScriptCategory.RANGED,
					new ImageIcon(image.getSubimage(0, 608, 32, 32)));
			icons.put(ScriptCategory.RUNECRAFTING,
					new ImageIcon(image.getSubimage(0, 640, 32, 32)));
			icons.put(ScriptCategory.SMITHING,
					new ImageIcon(image.getSubimage(0, 672, 32, 32)));
			icons.put(ScriptCategory.SUMMONING,
					new ImageIcon(image.getSubimage(0, 704, 32, 32)));
			icons.put(ScriptCategory.THIEVING,
					new ImageIcon(image.getSubimage(0, 736, 32, 32)));
			icons.put(ScriptCategory.WOODCUTTING,
					new ImageIcon(image.getSubimage(0, 768, 32, 32)));
		} catch(IOException exception) {}
		categoryIcons = Collections.unmodifiableMap(icons);
	}

	public ScriptLoaderFrame(Bot bot) {
		this(bot, null);
	}

	public ScriptLoaderFrame(Bot bot, Frame source) {
		super(source, "Scripts");
		manager = bot.getScriptManager();

		JMenuBar menuBar = new JMenuBar();
		GridBagLayout layout = new GridBagLayout();
		JPanel menuPanel = new JPanel(layout);
		layout.columnWidths = new int[] { 0, 0, 0, 125, 0, 0 };
		layout.rowHeights = new int[] { 0, 0 };
		layout.columnWeights = new double[] { 0.0, 0.0, 1.0, 0.0, 0.0, 1.0E-4 };
		layout.rowWeights = new double[] { 0.0, 1.0E-4 };

		JButton browseButton = new JButton(new ImageIcon(getClass()
				.getResource("/browse.png")));
		browseButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				browse();
			}
		});
		menuPanel.add(browseButton, new GridBagConstraints(0, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));

		JButton refreshButton = new JButton(new ImageIcon(getClass()
				.getResource("/reload.png")));
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		menuPanel.add(refreshButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));

		searchField = new JTextField();
		menuPanel.add(searchField, new GridBagConstraints(3, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 5), 0, 0));

		JButton searchButton = new JButton(new ImageIcon(getClass()
				.getResource("/search.png")));
		refreshButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				refresh();
			}
		});
		menuPanel.add(searchButton, new GridBagConstraints(4, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));

		menuBar.add(menuPanel);
		setJMenuBar(menuBar);

		scriptPane = new JPanel(new GridLayout(0, 2, 0, 0));
		GridBagLayout containerLayout = new GridBagLayout();
		JPanel scriptPaneContainer = new JPanel(containerLayout);
		containerLayout.columnWidths = new int[] { 0, 0 };
		containerLayout.rowHeights = new int[] { 0, 0 };
		containerLayout.columnWeights = new double[] { 1.0, 1.0E-4 };
		containerLayout.rowWeights = new double[] { 0.0, 1.0E-4 };
		scriptPaneContainer.add(scriptPane, new GridBagConstraints(0, 0, 1, 1,
				0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 0), 0, 0));
		add(new JScrollPane(scriptPaneContainer,
				JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
				JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

		setSize(500, 350);
		setLocationRelativeTo(getOwner());
		refresh();
		setVisible(true);
	}

	public void refresh() {
		manager.unloadScripts();
		manager.loadScripts();
		scriptPane.removeAll();

		System.out.println("Refreshing! " + selectedFile.getAbsolutePath());

		if(selectedFile == null
				|| !(selectedFile.isDirectory()
						|| selectedFile.getName().endsWith(".class") || selectedFile
						.getName().endsWith(".jar")))
			return;
		try {
			final Script[] scripts = manager.loadScripts(selectedFile.toURI()
					.toURL());
			for(Script script : scripts)
				System.out.println("Loaded script: "
						+ script.getManifest().name());
			for(Script script : scripts)
				scriptPane.add(new ScriptInfoPanel(script));
		} catch(Exception exception) {
			exception.printStackTrace();
		}
		revalidate();
	}

	public void browse() {
		File file = selectedFile != null ? (selectedFile.isDirectory() ? selectedFile
				: selectedFile.getParentFile())
				: null;
		JFileChooser fileChooser = new JFileChooser(file);
		fileChooser.setFileFilter(new FileFilter() {

			@Override
			public String getDescription() {
				return "*.class, *.jar";
			}

			@Override
			public boolean accept(File f) {
				return f.isDirectory() || f.getName().endsWith(".class")
						|| f.getName().endsWith(".jar");
			}
		});
		fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		fileChooser.setMultiSelectionEnabled(false);
		int result = fileChooser.showOpenDialog(this);
		if(result == JFileChooser.APPROVE_OPTION) {
			selectedFile = fileChooser.getSelectedFile();
			refresh();
		}
	}

	public void filter() {
	}

	private class ScriptInfoPanel extends JPanel {
		public ScriptInfoPanel(final Script script) {
			ScriptManifest manifest = script.getManifest();
			setBorder(new EtchedBorder());
			GridBagLayout layout = new GridBagLayout();
			setLayout(new GridBagLayout());
			layout.columnWidths = new int[] { 0, 0, 0 };
			layout.rowHeights = new int[] { 0, 0 };
			layout.columnWeights = new double[] { 0.0, 1.0, 1.0E-4 };
			layout.rowWeights = new double[] { 1.0, 1.0E-4 };

			JLabel label = new JLabel(categoryIcons.get(manifest.category()));
			add(label, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

			JPanel informationPanel = new JPanel(new GridLayout(0, 1, 10, 0));
			JLabel titleLabel = new JLabel(manifest.name());
			titleLabel.setFont(titleLabel.getFont().deriveFont(Font.BOLD,
					(float) (titleLabel.getFont().getSize() * 1.5)));
			JLabel versionLabel = new JLabel(manifest.version());
			versionLabel.setFont(versionLabel.getFont().deriveFont(
					(float) (versionLabel.getFont().getSize() * 1.5)));
			GridBagLayout titleLayout = new GridBagLayout();
			JPanel titlePanel = new JPanel(titleLayout);
			titleLayout.columnWidths = new int[] { 0, 0, 0 };
			titleLayout.rowHeights = new int[] { 0, 0 };
			titleLayout.columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
			titleLayout.rowWeights = new double[] { 0.0, 1.0E-4 };

			titlePanel.add(titleLabel, new GridBagConstraints(0, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
			titlePanel.add(new JLabel(manifest.version(), JLabel.TRAILING),
					new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
							GridBagConstraints.CENTER, GridBagConstraints.BOTH,
							new Insets(0, 0, 0, 0), 0, 0));
			informationPanel.add(titlePanel);
			informationPanel.add(new JLabel(manifest.description()));
			JButton runButton = new JButton(new ImageIcon(getClass()
					.getResource("/play.png")));
			runButton.setBorderPainted(false);
			runButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					script.start();
					ScriptLoaderFrame.this.setVisible(false);
				}
			});
			GridBagLayout buttonLayout = new GridBagLayout();
			JPanel buttonPanel = new JPanel(buttonLayout);
			buttonLayout.columnWidths = new int[] { 0, 0, 0 };
			buttonLayout.rowHeights = new int[] { 0, 0 };
			buttonLayout.columnWeights = new double[] { 1.0, 0.0, 1.0E-4 };
			buttonLayout.rowWeights = new double[] { 0.0, 1.0E-4 };

			buttonPanel.add(new JLabel(), new GridBagConstraints(0, 0, 1, 1,
					0.0, 0.0, GridBagConstraints.CENTER,
					GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
			buttonPanel.add(runButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
					0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			informationPanel.add(buttonPanel);
			add(informationPanel, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
		}
	}
}
