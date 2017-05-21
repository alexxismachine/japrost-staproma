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
 * SPM-File format with simple # / * notation.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class SimpleSpmFormat implements SpmFormat {
	private final TaskState status;

	/**
	 * Create an instance.
	 * 
	 * @param status
	 *            the status to assign to leafs.
	 */
	public SimpleSpmFormat(TaskState status) {
		this.status = status;
	}

	public Task parseLines(List<String> lines) {
		FolderTask rootTask = new FolderTask(null, "Root)");
		parseLines(rootTask, lines);
		return rootTask;
	}

	public void parseLines(Task rootTask, List<String> lines) {
		// FIXME Pattern matches single space -> nullpointer for rootTask
		Pattern p = Pattern.compile("(#*) (.*)");
		int currentL = 0;
		Task currentT = rootTask;
		Task contentT = rootTask;
		for (String line : lines) {
			Matcher m = p.matcher(line);
			if (m.matches()) {
				int level = (m.group(1).length());
				//System.out.println("-> Going for " + m.group(2) + " on " + level);
				if (currentL == level) {
					//System.out.println(" * Same");
					Task addTo = currentT.getParent();
					//System.out.println(" * Adding to " + addTo.getDescription());
					FolderTask task = new FolderTask(addTo, m.group(2));
					task.setState(status);
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
					FolderTask task = new FolderTask(addTo, m.group(2));
					task.setState(status);
					addTo.addChild(task);
					currentT = task;
					contentT = task;
				}
				if (currentL < level) {
					//System.out.println(" * Sub (" + (level - currentL) + ")");
					Task addTo = currentT;
					for (int l = 1; l < (level - currentL); l++) {
						AnonymousTask task = new AnonymousTask(addTo);
						task.setState(status);
						addTo.addChild(task);
						addTo = task;
					}
					//System.out.println(" * Adding to " + addTo.getDescription());
					FolderTask task = new FolderTask(addTo, m.group(2));
					task.setState(status);
					addTo.addChild(task);
					currentT = task;
					contentT = task;
				}
				currentL = level;
			} else if (line.startsWith("* ")) {
				//System.out.println("-> Going for " + line.substring(2) + " on " + currentL);
				//System.out.println(" * *");
				Task addTo = currentT;
				//System.out.println(" * Adding to " + addTo.getDescription());
				LeafTask task = new LeafTask(currentT, line.substring(2));
				task.setState(status);
				addTo.addChild(task);
				contentT = task;
			} else {
				contentT.addContent(line);
			}
			//System.out.println("<- Current (" + currentL + ") now " + currentT.getDescription());
		}
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
