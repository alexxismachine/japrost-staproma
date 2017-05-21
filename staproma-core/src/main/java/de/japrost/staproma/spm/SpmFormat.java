package de.japrost.staproma.spm;

import java.util.List;

import de.japrost.staproma.task.Task;

/**
 * A format for SPM-Files.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public interface SpmFormat {
	/**
	 * Parse the lines into a new root task.
	 * 
	 * @param lines
	 *            the lines to parse
	 * @return the root task containing the converted lines.
	 */
	@Deprecated
	public Task parseLines(List<String> lines);

	/**
	 * Parse the lines into the given root task.
	 * 
	 * @param rootTask
	 *            the root task to add the lines to.
	 * @param lines
	 *            the lines to parse.
	 */
	public void parseLines(Task rootTask, List<String> lines);
}
