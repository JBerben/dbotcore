package org.darkstorm.runescape.ui;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.Queue;
import java.util.logging.*;
import java.util.logging.Formatter;

import javax.swing.*;
import javax.swing.Timer;
import javax.swing.border.*;

@SuppressWarnings("serial")
public class LogPane extends JList<LogRecord> {
	public static final int MAX_ENTRIES = 100;
	public static final Rectangle BOTTOM_OF_WINDOW = new Rectangle(0,
			Integer.MAX_VALUE, 0, 0);

	private final LogQueue queue;
	private final DefaultListModel<LogRecord> model;
	private final Formatter formatter = new LogFormatter();

	public LogPane(Logger logger) {
		queue = new LogQueue();
		model = new DefaultListModel<LogRecord>();
		setModel(model);
		setCellRenderer(new LogRecordCellRenderer());

		logger.addHandler(new LogHandler());
	}

	private class LogRecordCellRenderer implements ListCellRenderer<LogRecord> {
		private final Border EMPTY_BORDER = new EmptyBorder(1, 1, 1, 1);
		private final Border SELECTED_BORDER = UIManager
				.getBorder("List.focusCellHighlightBorder");
		private final Color DARK_GREEN = new Color(0, 90, 0);

		@Override
		public Component getListCellRendererComponent(
				JList<? extends LogRecord> list, LogRecord record, int index,
				boolean isSelected, boolean cellHasFocus) {
			final JTextArea result = new JTextArea(formatter.format(record));
			System.out.println("Rendering!: " + result.getText());
			// result.setDragEnabled(true);
			// result.setText(formatter.format(record));
			result.setComponentOrientation(list.getComponentOrientation());
			result.setFont(list.getFont());
			result.setBorder(cellHasFocus || isSelected ? SELECTED_BORDER
					: EMPTY_BORDER);

			result.setForeground(Color.DARK_GRAY);
			result.setBackground(Color.WHITE);

			if(record.getLevel() == Level.SEVERE) {
				result.setBackground(Color.RED);
				result.setForeground(Color.WHITE);
			}

			if(record.getLevel() == Level.WARNING) {
				result.setBackground(Color.YELLOW);
			}

			if((record.getLevel() == Level.FINE)
					|| (record.getLevel() == Level.FINER)
					|| (record.getLevel() == Level.FINEST)) {
				result.setForeground(DARK_GREEN);
			}

			Object[] parameters = record.getParameters();
			if(parameters != null) {
				for(Object parameter : parameters) {
					if(parameter == null) {
						continue;
					}

					if(parameter instanceof Color) {
						result.setForeground((Color) parameter);
					} else if(parameter instanceof Font) {
						result.setFont((Font) parameter);
					}
				}
			}

			return result;
		}
	}

	private class LogHandler extends Handler {
		@Override
		public void publish(LogRecord record) {
			queue.offer(record);
		}

		@Override
		public void flush() {
		}

		@Override
		public void close() throws SecurityException {
		}
	}

	private class LogQueue {
		private final Queue<LogRecord> queue;

		public LogQueue() {
			queue = new ArrayDeque<LogRecord>();

			Timer timer = new Timer(200, new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					update();
				}
			});
			timer.setRepeats(true);
			timer.setCoalesce(true);
			timer.start();
		}

		public void offer(LogRecord item) {
			synchronized(queue) {
				queue.offer(item);
			}
		}

		private void update() {
			boolean visible = true;
			Point location = indexToLocation(model.size() - 1);
			Rectangle area = getVisibleRect();
			if(location != null && area != null)
				visible = area.contains(location);
			synchronized(queue) {
				while(!queue.isEmpty()) {
					while(model.getSize() > 200)
						model.removeElementAt(0);
					model.addElement(queue.poll());
				}
			}
			if(visible)
				ensureIndexIsVisible(model.size() - 1);
		}
	}

	private class LogFormatter extends Formatter {
		@Override
		public String format(LogRecord record) {
			StringBuilder formatted = new StringBuilder();
			formatted.append("[").append(record.getLevel().getName())
					.append("] ");
			formatted.append(new Date(record.getMillis())).append(": ");
			formatted.append(record.getLoggerName()).append(": ");
			formatted.append(record.getMessage());
			Throwable thrown = record.getThrown();
			if(thrown != null) {
				StringWriter writer = new StringWriter();
				thrown.printStackTrace(new PrintWriter(writer));
				formatted.append(writer.toString());
			}
			return formatted.toString();
		}
	}
}
