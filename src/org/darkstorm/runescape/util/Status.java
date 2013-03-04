package org.darkstorm.runescape.util;

public interface Status {
	public boolean isProgressShown();

	public void setProgressShown(boolean progressShown);

	public int getProgress();

	public void setProgress(int progress);

	public String getMessage();

	public void setMessage(String message);
}
