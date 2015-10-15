package de.japrost.staproma.task;

import java.util.Collection;

/**
 * <p>
 * A task in the task tree.
 * </p>
 * <p>
 * A task consists mainly of two parts: the description and the content. In addition to this,for the task tree, the task
 * can have a parent task and child tasks. A task implements {@link Iterable} on its children.
 * </p>
 * <p>
 * A task has no concrete state since for a task with children the task itself is supposed to be in all the states of
 * the children.
 * </p>
 * <p>
 * The content of the task is just a bunch of Strings that can be collected and returned.
 * </p>
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
// TODO allow usage of an internal Iterator (eg, to do the distinction between depth of breadth first iteration).
// TODO? devide the "contents"-part and the tree-parts into two interfaces.
public interface Task extends Iterable<Task> {
	/**
	 * Do not like the string parameter.<br>
	 * Tell if the task is in the asked status. A task is in a status if itself or one of the children is in that
	 * status. If given status is {@code null} must return <code>true</code>.
	 * 
	 * @param status
	 *            the task type
	 * @return <code>true</code> if the task is in the asked status.
	 */
	boolean isInState(String status);

	/**
	 * Add a child to the task. (??? Sets the parent of the added task to this???).
	 * 
	 * @param task
	 *            the task to add.
	 */
	void addChild(Task task);

	/**
	 * Get the tasks description.
	 * 
	 * @return the description.
	 */
	String getDescription();

	/**
	 * Tell if there are children to the task.
	 * 
	 * @return <code>true</code> if there are children.
	 */
	boolean hasChildren();

	/**
	 * Get the praent task.
	 * 
	 * @return the parent task. <code>null</code> if there is no parent.
	 */
	Task getParent();

	/**
	 * Add a line to the content collection.
	 * 
	 * @param line
	 *            the line to add.
	 */
	void addContent(String line);

	/**
	 * get the content collection.
	 * 
	 * @return the content collection. MUST NOT be <code>null</code>. MAY be an unmodifiable collection.
	 * 
	 */
	Collection<String> getContent();
	/* FIXME implement correct rendering
	/**
	 * Render this task into given writer using its own representation.
	 * 
	 * @param writer
	 *            the writer to write to.
	 * @param level
	 *            the level this task is supposed to be in.
	 * @throws IOException
	 *             if writing to the writer failes.
	 /
	void render(Writer writer, int level) throws IOException;
	*/
}
