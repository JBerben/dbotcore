package org.darkstorm.runescape.api.input;

public interface Keyboard {
	public void typeKey(char key);

	public void typeMessage(String message);

	public void typeMessage(String message, int delay);

	public void typeMessage(String message, int delayLow, int delayHigh);

	public void holdKey(char key);

	public boolean isActive();

	public void stop();

	public boolean await();

	public boolean await(int timeout);
}
