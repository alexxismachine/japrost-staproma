package de.japrost.staproma;

/**
 * State of a Task.
 */
public enum TaskState {
	/** Task is to do. */
	CURRENT, 
	/** Task is waiting for someone else. */
	WAITING, 
	/** Task is scheduled some time. */
	SCHEDULE, 
	/** Task is to do in future (a very likely kind of maybe in GTD). */
	FUTURE, 
	/** Task is to do some day (a kind of maybe in GTD with a lot of questionmarks). */
	SOMEDAY, 
	/** Task is done. */
	DONE;

}
