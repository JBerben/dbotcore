package org.darkstorm.runescape.api.input;

import org.darkstorm.runescape.api.*;

public interface Keyboard extends Utility {
	public void typeKey(char key);

	public void typeMessage(String message, boolean pressEnter);

	public void pressKey(char key);

	public void releaseKey(char key);

	public void holdKey(char key, int duration);

	public boolean isActive();

	public void stop();

	public boolean await();

	public boolean await(int timeout);

	public GameContext getGameContext();
}
