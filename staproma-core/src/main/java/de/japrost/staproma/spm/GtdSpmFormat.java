package de.japrost.staproma.spm;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.AnonymousTask;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * <p>
 * SPM-file format with GTD notation.
 * </p>
 * <p>
 * This notation is devided in two parts: topics and actions. A topic represents a project in GTD (something that
 * requires more than one action to take place). An action represents one single step in a project. Typical for GTD are
 * the next steps.
 * </p>
 * <p>
 * Topics are all lines that start with one or more {@code #} followed by a SPACE. The remains of the line describes the
 * topic. The number of hashes describe a hierarchy of topics. Actions are all lines that start with a single {@code *}
 * followed by a SPACE, followed by the action symbol in braces followed by a SPACE. The remains of the line describes
 * the action. All other lines are supposed to be task content.
 * </p>
 * <em>Action symbols</em>
 * <p>
 * The following action symbols are supported:
 * <table>
 * <caption>Supported symbols</caption>
 * <tr>
 * <th>Symbol</th>
 * <th>Meaning</th>
 * <th>State</th>
 * <th>Remark</th>
 * </tr>
 * <tr>
 * <td>{@code !}</td>
 * <td>Something to do (a next step).</td>
 * <td>{@code CURRENT}</td>
 * <td>This next step has priority 1. {@code '!'} is equivalent to {@code '1')}.</td>
 * </tr>
 * <tr>
 * <td>{@code (n)}</td>
 * <td>Something to do (a next step) with the priority {@code n}.. {@code (1)} and {@code (!)} are equivalent.</td>
 * <td>{@code CURRENT}</td>
 * <td>{@code 'n'} ranges from 1 to 9. 0 means unknown.<br> {@code '1'} is equivalent to {@code '!')}.</td>
 * </tr>
 * <tr>
 * <td>{@code @}</td>
 * <td>Something on a schedule.</td>
 * <td>{@code SCHEDULE}</td>
 * </tr>
 * <tr>
 * <td>{@code /}</td>
 * <td>Something that is done already.</td>
 * <td>{@code DONE}</td>
 * </tr>
 * <tr>
 * <td>{@code ?}</td>
 * <td>Something maybe/somtime (future project).</td>
 * <td>{@code FUTURE}</td>
 * </tr>
 * <tr>
 * <td>{@code #}</td>
 * <td>Something to do in future (a future next step).</td>
 * <td>{@code FUTURE}</td>
 * </tr>
 * <tr>
 * <td>{@code :}</td>
 * <td>Something to wait for someone other fullfills it.</td>
 * <td>{@code WAITING}</td>
 * </tr>
 * </table>
 * All other lines starting with an asteriks and a SPACE will be used as {@code CURRENT} tasks to avoid that typos
 * result in missing tasks. Example:
 * 
 * <pre>
 * {@code
 * # Topic on level one
 * * (!) a next step
 * * (4) a next step with priority 4
 * * (/) something that is done
 * * (#) something to to in some future
 * ## Topic on level two (subtopic)
 * * (:) waiting for someone
 * * (?) maybe do this some time?
 * ** This is some content. Maybe describing the action before
 * This is another content for the maybe action
 * * (@) This action is scheduled
 * # Second topic
 * * The missing symbol will create a next step
 * }
 * </pre>
 * 
 * @author alexxismachine (Ulrich David)
 */
public class GtdSpmFormat implements SpmFormat {
	/**
	 * Pattern to match against "topics".
	 */
	private static final Pattern TOPIC_PATTERN = Pattern.compile("(#*) (.*)");
	/**
	 * Pattern to match against "actions" to take place. The {@code \f} is an additional current task (like a unknown
	 * symbol) for unit testing.
	 */
	private static final Pattern ACTION_PATTERN = Pattern.compile("\\* \\(([!/?#:@\\d\\f])\\) (.*)");

	/**
	 * Parse the lines into a new root task.
	 * 
	 * @param lines
	 *            the lines to parse
	 * @return the root task containing the converted lines.
	 */
	public Task parseLines(List<String> lines) {
		FolderTask rootTask = new FolderTask(null, "Root)");
		parseLines(rootTask, lines);
		return rootTask;
	}

	/**
	 * Parse the lines into the given root task.
	 * 
	 * @param rootTask
	 *            the root task to add the lines to.
	 * @param lines
	 *            the lines to parse.
	 */
	// TODO ? try this with a parser framework (ANTLR?)
	public void parseLines(Task rootTask, List<String> lines) {
		int currentL = 0;
		Task currentT = rootTask;
		Task contentT = rootTask;
		for (String line : lines) {
			Matcher topicMatcher = TOPIC_PATTERN.matcher(line);
			if (topicMatcher.matches()) {
				int level = (topicMatcher.group(1).length());
				System.out.println("#> Going for " + topicMatcher.group(2) + " on " + level);
				if (currentL == level) {
					//System.out.println(" * Same");
					Task addTo = currentT.getParent();
					//System.out.println(" * Adding to " + addTo.getDescription());
					FolderTask task = new FolderTask(addTo, topicMatcher.group(2));
					// task.setState(status); 
					addTo.addChild(task);
					currentT = task;
					contentT = task;
				}
				if (currentL > level) {
					//System.out.println(" * Parent (" + (level - currentL) + ")");
					Task addTo = currentT.getParent().getParent();
					for (int l = 1; l < (currentL - level); l++) {
						addTo = addTo.getParent();
					}
					//System.out.println(" * Adding to " + addTo.getDescription());
					FolderTask task = new FolderTask(addTo, topicMatcher.group(2));
					// task.setState(status);
					addTo.addChild(task);
					currentT = task;
					contentT = task;
				}
				if (currentL < level) {
					//System.out.println(" * Sub (" + (level - currentL) + ")");
					Task addTo = currentT;
					for (int l = 1; l < (level - currentL); l++) {
						AnonymousTask task = new AnonymousTask(addTo);
						// task.setState(status);
						addTo.addChild(task);
						addTo = task;
					}
					//System.out.println(" * Adding to " + addTo.getDescription());
					FolderTask task = new FolderTask(addTo, topicMatcher.group(2));
					//task.setState(status);
					addTo.addChild(task);
					currentT = task;
					contentT = task;
				}
				currentL = level;
				continue; // replace with else later on?
			}
			Matcher stepMatcher = ACTION_PATTERN.matcher(line);
			if (stepMatcher.matches()) {
				System.out.println("*> Going for '" + line.substring(2) + "' with '" + stepMatcher.group(1) + "' on "
						+ currentL);
				String symbol = stepMatcher.group(1);
				TaskState state = null;
				short priority = 0;
				if ("!".equals(symbol)) {
					state = TaskState.CURRENT;
					priority=1;
				} else if ("@".equals(symbol)) {
					state = TaskState.SCHEDULE;
				} else if ("/".equals(symbol)) {
					state = TaskState.DONE;
				} else if ("?".equals(symbol)) {
					state = TaskState.SOMEDAY;
				} else if ("#".equals(symbol)) {
					state = TaskState.FUTURE;
				} else if (":".equals(symbol)) {
					state = TaskState.WAITING;
				} else if ((priority =  parseNumber(symbol))!=0) {
					state = TaskState.CURRENT;
				} else {
					// match but unknown symbol? Can only happen on changed action pattern!
					state = null;
				}
				LeafTask task;
				if (state == null) {
					// No symbol found. Should not happen!
					task = new LeafTask(currentT, line.substring(2));
					task.setState(TaskState.CURRENT);
				} else {
					task = new LeafTask(currentT, stepMatcher.group(2));
					task.setState(state);
					task.setPriority(priority);
				}
				//System.out.println(" * *");
				Task addTo = currentT;
				//System.out.println(" * Adding to " + addTo.getDescription());
				addTo.addChild(task);
				contentT = task;
				continue; // replace with else later on?
			}
			if (line.startsWith("* ")) {
				System.out.println("-> Going for " + line.substring(2) + " on " + currentL);
				//System.out.println(" * *");
				Task addTo = currentT;
				//System.out.println(" * Adding to " + addTo.getDescription());
				LeafTask task = new LeafTask(currentT, line.substring(2));
				task.setState(TaskState.CURRENT);
				addTo.addChild(task);
				contentT = task;
				continue; // replace with else later on?
			}
			// no match -> must be content
			// FIXME do not add content to root task!
			System.out.println(" > Going for " + line + " on " + currentL);
			contentT.addContent(line);
			//System.out.println("<- Current (" + currentL + ") now " + currentT.getDescription());
		}
	}
	
	private short parseNumber(String symbol) {
		short result = 0;
		try {
			result = Short.parseShort(symbol);
		} catch (NumberFormatException e){
			// nothing to do
		}
		return result;
	}
	/* FIXME do formating 
		public List<String> formatTasks(Task task) {
			List<String> lines = new ArrayList<String>();
			doPrintSubTree(task, 0, lines);
			return lines;
		}

		public void renderTasks(Task task, Writer writer) throws IOException {
			renderTasks(task, writer, null);
		}

		public void renderTasks(Task task, Writer writer, String status) throws IOException {
			// FIXME this should render the tasks into their natural representation.
			doRenderSubTree(task, 0, writer, status);
		}

		private List<String> doPrintSubTree(Task task, int level, List<String> lines) {
			int myLevel = level + 1;
			for (Task subTask : task) {
				if (subTask instanceof AnonymousTask) {
					// skip anonymous on output
				} else {
					String line = "";
					if (subTask instanceof FolderTask) {
						for (int i = 0; i < myLevel; i++) {
							//System.out.print("#");
							line = line + "#";
						}
					} else {
						//System.out.print("*");
						line = line + "*";
					}
					line = line + " " + subTask.getDescription();
					//System.out.println(line);
					lines.add(line);
					lines.addAll(subTask.getContent());
				}
				//System.out.println(" " + subTask.getDescription() + " '" + myLevel + "'");
				//System.out.println("C " + subTask.getContent());
				doPrintSubTree(subTask, myLevel, lines);
			}
			return lines;
		}

		private void doRenderSubTree(Task task, int level, Writer writer, String status) throws IOException {
			System.out.println(level + "-> Render for " + task.getDescription() + " in state " + status);
			int myLevel = level + 1;
			if (task.isInState(status)) {
				System.out.println(level + "+> Render for " + task.getDescription() + " in state " + status);
				System.out.println("   write " + task.getDescription() + "\n");
				task.render(writer, level);
				for (Task subTask : task) {
					doRenderSubTree(subTask, myLevel, writer, status);
				}
			} else {
				System.out.println(level + "<- Render for " + task.getDescription());
			}
		}
		*/
}
