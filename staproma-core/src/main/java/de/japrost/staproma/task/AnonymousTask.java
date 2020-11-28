package de.japrost.staproma.task;

/**
 * Represents an intermediate task. Usable if there is a level gap in the notation e.g.:<br>
 * 
 * <pre>
 * # Level 1
 * ### Level 3
 * </pre>
 * 
 * Place the {@link AnonymousTask} between Level 1 and Level 3 to have a valid tree.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class AnonymousTask extends AbstractContainerTask {
	/**
	 * The task description of the {@link AnonymousTask}.
	 */
	public static final String ANONYMOUS_TASK_DESCRIPTION = "N/A";

	/**
	 * Create with given parent.
	 * 
	 * @param parent
	 *            the parent task.
	 */
	public AnonymousTask(Task parent) {
		super(parent, ANONYMOUS_TASK_DESCRIPTION);
	}
	/*
	 * This implementation does nothing.
	@Override
	public void render(Writer writer, int level) {
	}
	 */
}
