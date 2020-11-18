package de.japrost.staproma;

/**
 * Filenames of the tasks states.
 */
public enum TaskStateFileName {

	/** Task is to do. */
	CURRENT(TaskState.CURRENT, "01_currentItems.html"),
	/** Task is waiting for someone else. */
	WAITING(TaskState.WAITING, "02_waitingItems.html"),
	/** Task is scheduled some time. */
	SCHEDULE(TaskState.SCHEDULE, "03_scheduledItems.html"),
	/** Task is to do in future (a very likely kind of maybe in GTD). */
	FUTURE(TaskState.FUTURE, "10_futureItems.html"),
	/** Task is to do some day (a kind of maybe in GTD with a lot of questionmarks). */
	SOMEDAY(TaskState.SOMEDAY, "20_somedayItems.html"),
	/** Task is done. */
	DONE(TaskState.DONE, "99_doneItems.html");

	private final TaskState taskState;
	private final String fileName;

	private TaskStateFileName(final TaskState taskState, final String fileName) {
		this.taskState = taskState;
		this.fileName = fileName;
	}

	public TaskState taskState() {
		return taskState;
	}

	public String fileName() {
		return fileName;
	}
}
