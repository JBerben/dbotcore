package org.darkstorm.runescape.oldschool;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.swing.SwingUtilities;

import org.darkstorm.runescape.util.Status;

public class ImageStatus implements Status {
	private boolean progressShown = false;
	private int progress = 0;
	private String message = "";

	private BufferedImage image;
	private Graphics2D g;
	private Font font;

	public ImageStatus(BufferedImage image) {
		font = new Font("Arial", Font.BOLD, 12);
		this.image = image;
		g = image.createGraphics();
	}

	@Override
	public boolean isProgressShown() {
		return progressShown;
	}

	@Override
	public int getProgress() {
		return progress;
	}

	@Override
	public String getMessage() {
		return message;
	}

	@Override
	public void setProgressShown(boolean progressShown) {
		this.progressShown = progressShown;
		repaint();
	}

	@Override
	public void setProgress(int progress) {
		if(progress < 0)
			progress = 0;
		if(progress > 100)
			progress = 100;
		this.progress = progress;
		repaint();
	}

	@Override
	public void setMessage(String message) {
		this.message = message;
		repaint();
	}

	public void repaint() {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				AffineTransform originalTransform = g.getTransform();

				int width = image.getWidth(), height = image.getHeight();

				g.setColor(Color.BLACK);
				g.fillRect(0, 0, width, height);
				Font largeFont = new Font("Arial", Font.BOLD, 36);
				g.setFont(largeFont);
				String title = "DarkBot";
				g.setColor(Color.WHITE);
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
				g.drawString(title,
						width / 2 - g.getFontMetrics().stringWidth(title) / 2,
						height / 2 - 100);
				g.drawLine(width / 2 - g.getFontMetrics().stringWidth(title)
						/ 2, height / 2 - 100 + g.getFontMetrics().getDescent()
						/ 2, width / 2 + g.getFontMetrics().stringWidth(title)
						/ 2, height / 2 - 100 + g.getFontMetrics().getDescent()
						/ 2);
				title = "vRS07  r0001";
				int descent = g.getFontMetrics().getDescent();
				g.setFont(largeFont.deriveFont(Font.PLAIN, 24F));
				g.drawString(title,
						width / 2 - g.getFontMetrics().stringWidth(title) / 2,
						height / 2 - 100 + g.getFontMetrics().getHeight());
				g.drawLine(width / 2, height / 2 - 100 + descent / 2,
						width / 2, height / 2 - 100
								+ g.getFontMetrics().getHeight());
				g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
						RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
				g.setFont(font);

				g.setColor(new Color(140, 17, 17));
				g.setStroke(new BasicStroke(1f));
				int progressWidth = 300, progressHeight = 35;
				g.translate(width / 2 - progressWidth / 2, height / 2
						- progressHeight / 2);
				if(progressShown) {
					g.drawRect(0, 0, progressWidth, progressHeight);
					double ratio = progress / 100D;
					g.fillRect(2, 2,
							(int) Math.round((progressWidth - 3) * ratio),
							progressHeight - 3);
				}

				if(message != null) {
					g.setColor(Color.WHITE);
					FontMetrics metrics = g.getFontMetrics();
					g.drawString(message,
							progressWidth / 2 - metrics.stringWidth(message)
									/ 2,
							progressHeight / 2 + metrics.getHeight() / 2);
				}

				g.setTransform(originalTransform);
			}
		});
	}
}