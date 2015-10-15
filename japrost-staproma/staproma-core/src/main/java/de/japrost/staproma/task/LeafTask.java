package de.japrost.staproma.task;

/**
 * Represents a task without level (no sub tasks).
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
// FIXME make this a marker interface with the get status method.
public class LeafTask extends AbstactTask {
	/**
	 * Create an instance with the given parameters.
	 * 
	 * @param parent
	 *            the parent task.
	 * @param description
	 *            the description.
	 */
	public LeafTask(Task parent, String description) {
		super(parent, description);
	}

	@Override
	public void addChild(Task task) {
		throw new IllegalArgumentException("Cannot add to leaf");
	}
	/*
	 * Since no children status is unique.
	 * 
	 * @return the status.
	 * 
	 *
	public String getStatus() {
		return this.state;
	}
	*/
	/*
	 * This implementation appends just a * and the description.
	@Override
	public void render(Writer writer, int level) throws IOException {
		writer.append("* " + getDescription());
		writer.append("\n");
		// do not display ideas on what to do.
		//		for (String content : getContent()) {
		//			writer.append("\n");
		//			writer.append(content);
		//			writer.append("\n");
		//		}
	}
	 */
}
