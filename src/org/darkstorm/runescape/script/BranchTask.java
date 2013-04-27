package org.darkstorm.runescape.script;

public class BranchTask implements Task {
	private final Task[] tasks;
	private Task activeTask;

	public BranchTask(Task... tasks) {
		this.tasks = tasks.clone();
	}

	@Override
	public synchronized boolean activate() {
		for(int i = 0; i < tasks.length; i++) {
			Task task = tasks[i];
			if(!task.activate())
				continue;
			activeTask = task;
			return true;
		}
		activeTask = null;
		return false;
	}

	@Override
	public synchronized void run() {
		activeTask.run();
	}

	public Task[] getTasks() {
		return tasks.clone();
	}

	public Task getActiveTask() {
		return activeTask;
	}
}
