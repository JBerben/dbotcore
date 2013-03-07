package org.darkstorm.runescape.script;

public interface TaskManager {
	public void register(Task task);

	public void deregister(Task task);

	public boolean isActive(Task task);

	public void stop(Task task);

	public Task[] getRegisteredTasks();

	public Task[] getActiveTasks();
}
