package de.japrost.staproma.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * Renderer which tries to render tasks by date.
 */
public class ScheduledTaskHtmlRenderer {
	private final Task rootTask;
	private final Writer writer;
	private List<LeafTask> leafs;

	/**
	 * Create an initialized instance.
	 * 
	 * @param rootTask
	 *            the task to render.
	 * @param writer
	 *            the writer to render to.
	 */
	public ScheduledTaskHtmlRenderer(Task rootTask, Writer writer) {
		if (rootTask == null | writer == null) {
			throw new IllegalArgumentException("Task (" + rootTask + ") or Writer (" + writer + ") must not be null");
		}
		this.rootTask = rootTask;
		this.writer = writer;
		leafs = new ArrayList<>();
	}

	/**
	 * Render the tasks to the writer.
	 * 
	 * @throws IOException
	 *             on io failures on the writer.
	 */
	public void render() throws IOException {
		writer.write("<dl>\n");
		if (rootTask instanceof LeafTask) {
			render((LeafTask) rootTask);
		} else {
			collectFromTree(rootTask);
			leafs.sort(new Comparator<LeafTask>() {

				@Override
				public int compare(LeafTask o1, LeafTask o2) {
					return o1.getDescription().compareTo(o2.getDescription());
				}
			});
			renderLeafs();
		}
		writer.write("</dl>\n");
	}

	private void collectFromTree(Task treeRoot) throws IOException {
		for (Task subTask : treeRoot) {
			if (subTask.isInState(TaskState.SCHEDULE)) {
				if (subTask instanceof LeafTask) {
					leafs.add((LeafTask) subTask);
				} else {
					collectFromTree(subTask);
				}
			}
		}
	}

	private void renderLeafs() throws IOException {
		for (LeafTask leaf : leafs) {
			render(leaf);
		}
	}

	private void render(LeafTask leafTask) throws IOException {
		String parentString = composeParentPath(leafTask);
		String[] split = leafTask.getDescription().split(" ", 2);
		writer.write("  <dt>" + split[0] + "</dt>\n");
		writer.write("  <dd>" + split[1] + parentString + "</dd>\n");
		writer.write("\n");
	}

	private String composeParentPath(Task leafTask) {
		Task parent = leafTask.getParent();
		String parentString = "";
		if (parent != null && parent.getParent() != null) {
			// FIXME create links to directory tasks.
			if (parent instanceof DirectoryTask) {
				parentString = " : <a href='" + ((DirectoryTask) parent).getPath() + "'>" + parent.getDescription() + "</a>";
			} else {
				parentString = " : " + parent.getDescription();
			}
			parentString = parentString + composeParentPath(parent);
		}
		return parentString;
	}
}
