package org.darkstorm.runescape.script;

public interface Task extends Runnable {
	public boolean activate();

	@Override
	public void run();
}
