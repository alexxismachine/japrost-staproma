package de.japrost.staproma.task;

/**
 * Represents a task with a level inside a document.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class FolderTask extends AbstractContainerTask {
	/**
	 * Create an instance with the given parameters.
	 * 
	 * @param parent
	 *            the parent task.
	 * @param description
	 *            the description.
	 */
	public FolderTask(Task parent, String description) {
		super(parent, description);
	}
	/*
	 * This implementation appends just a * and the description.
	@Override
	public void render(Writer writer, int level) throws IOException {
		for (int i = 0; i < level; i++) {
			writer.append("#");
		}
		writer.append(" " + getDescription());
		writer.append("\n");
		writer.append("\n");
		// do not display ideas on what to do.
		//		for (String content : getContent()) {
		//			writer.append(content);
		//			writer.append("\n");
		//		}
	}
	 */
}
