package org.darkstorm.runescape.script;

public interface TaskManager {
	public void register(Task task);

	public void deregister(Task task);

	public boolean isActive(Task task);

	public void stop(Task task);

	public <T extends Task> T getTask(Class<T> taskClass);

	public Task[] getRegisteredTasks();

	public Task[] getActiveTasks();
}
