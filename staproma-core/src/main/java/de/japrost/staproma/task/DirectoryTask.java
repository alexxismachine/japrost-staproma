package de.japrost.staproma.task;

import de.japrost.staproma.TaskState;

/**
 * Represents a task which is a directory (so not derived form parsing a file).<br>
 * A directory has no state of its own but is in all the states that the containing tasks are.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class DirectoryTask extends AbstractContainerTask {
	private final String path;

	/**
	 * Create a DirectoryTask with the given parameters.
	 * 
	 * @param parent
	 *            the parent task.
	 * @param path
	 *            the directory which is represented by this task.
	 * @param description
	 *            the description of the task.
	 */
	public DirectoryTask(Task parent, String path, String description) {
		super(parent, description);
		this.path = path;
	}

	/**
	 * {@inheritDoc}
	 * <p>
	 * This implementation first asks the subtasks if one of them is in the state. If there are no sub tasks returns
	 * {@code true}, so an empty directory is in every state. After that the own state field is compared with the
	 * status. If still no match, {@code false} will be returned.
	 * </p>
	 */
	@Override
	public boolean isInState(TaskState status) {
		String enter = "-> state " + getDescription();
		String leave = "<- state " + getDescription() + ": ";
		System.out.println(enter);
		if (status == null) {
			System.out.println(leave + "true on null");
			return true;
		}
		for (Task subTask : subTasks) {
			if (subTask.isInState(status)) {
				System.out.println(leave + "true on sub");
				return true;
			}
		}
		if (subTasks.isEmpty()) {
			System.out.println(leave + "true on empty sub");
			return true;
		}
		if (status.equals(state)) {
			System.out.println(leave + "true on own");
			return true;
		}
		System.out.println(leave + "false on fallback");
		return false;
	}

	/*
	 * This implementation appends just a * and the description.
	@Override
	public void render(Writer writer, int level) throws IOException {
		writer.append("\n");
		for (int i = 0; i < level; i++) {
			writer.append("#");
		}
		// FIXME get file name
		writer.append(" [" + getDescription() + "](" + file.getPath() + ")");
		writer.append("\n");
		writer.append("\n");
		for (String content : getContent()) {
			writer.append(content);
			writer.append("\n");
		}
	}
	 */
	/**
	 * Get the path of the filename.
	 * 
	 * @return the path.
	 */
	//TODO this is used to create links: allow for relative links.
	public String getPath() {
		return path;
	}
}
