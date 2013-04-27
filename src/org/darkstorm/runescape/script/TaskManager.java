package org.darkstorm.runescape.script;

public interface TaskManager<T extends Task> {
	public void register(T task);

	public void deregister(T task);

	public boolean isActive(T task);

	public void stop(T task);

	public <A extends T> A getTask(Class<A> taskClass);

	public T[] getRegisteredTasks();

	public T[] getActiveTasks();
}
