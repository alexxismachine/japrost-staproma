package de.japrost.staproma.renderer;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.stream.Collectors;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * Renderer to create HTML output from {@link Task}s.
 *
 * @author alexxismachine (Ulrich David)
 */
public class StatusTaskHtmlRenderer implements Renderer {

	private final Task root;
	private final Writer writer;
	private final TaskState taskState;

	public StatusTaskHtmlRenderer(final Task root, final TaskState taskState, final Writer writer) {
		this.root = root;
		this.writer = writer;
		this.taskState = taskState;
	}

	/**
	 * Do the rendering of the given task with its subtasks in status to the writer.
	 *
	 * @param root the root task to start rendering from. MUST NOT be {@code null}.
	 * @param taskState the status tasks must be in for rendering.
	 * @param writer the writer to render to. MUST NOT be {@code null}.
	 * @throws IOException on io failures on the writer.
	 */
	@Override
	public void render() throws IOException {
		//TODO writer and status as instance variables
		walkSubTree(root, 0, new RenderState(), writer);
	}

	private void walkSubTree(final Task task, final int level, final RenderState renderState,
			final Writer writer)
			throws IOException {
		int myLevel = level + 1;
		if (task.isInState(taskState)) {
			if (level > 0) {
				// do not print root task
				if (task instanceof LeafTask) {
					printLeafTask(task, level, renderState, writer);
				} else {
					printOtherTask(task, level, renderState, writer);
				}
			}
			RenderState localRenderState = new RenderState();
			for (Task subTask : task) {
				walkSubTree(subTask, myLevel, localRenderState, writer);
			}
			if (localRenderState.openul) {
				writer.append("</ul>\n");
			}
		}
	}

	private void printLeafTask(final Task task, final int level, final RenderState renderState, final Writer writer)
			throws IOException {
		if (!renderState.openul) {
			renderState.openul = true;
			if (renderState.inBlock) {
				writer.append("<h" + level + ">" + "WEITERES" + "</h" + level + ">\n");
			}
			writer.append("<ul>\n");
		}
		if (task.getPriority() > 0) {
			writer.append("  <li class='priority" + task.getPriority() + "'>" + task.getDescription() + "</li>\n");
		} else {
			writer.append("  <li>" + task.getDescription() + "</li>\n");
		}
	}

	private void printOtherTask(final Task task, final int level, final RenderState renderState, final Writer writer)
			throws IOException {
		if (renderState.openul) {
			writer.append("</ul>\n");
			renderState.openul = false;
		}
		renderState.inBlock = true;
		List<Short> priorities = task.priorities();
		System.out.println("##########" + priorities);
		String prioClasses;
		if (priorities.isEmpty()) {
			prioClasses = "";
		} else {
			prioClasses = priorities.stream().filter(s -> s.shortValue() != 0)
					.map(s -> "priority" + s.toString())
					.collect(Collectors.joining(" ", " class='", "'"));
		}
		if (task instanceof DirectoryTask) {
			writer.append("<h" + level + prioClasses + ">");
			writer.append("<a href='" + ((DirectoryTask) task).getPath() + "'>");
			writer.append(task.getDescription());
			writer.append("</a>");
			writer.append("</h" + level + ">\n");
		} else {
			writer.append("<h" + level + prioClasses + ">" + task.getDescription() + "</h" + level + ">\n");
		}
	}

	private void printTask(final Task task, final int level, final RenderState renderState, final Writer writer)
			throws IOException {
		// TODO somehow with inheritance?
		if (task instanceof LeafTask) {
			if (!renderState.openul) {
				renderState.openul = true;
				if (renderState.inBlock) {
					writer.append("<h" + level + ">" + "WEITERES" + "</h" + level + ">\n");
				}
				writer.append("<ul>\n");
			}
			if (task.getPriority() > 0) {
				writer.append("  <li class='priority" + task.getPriority() + "'>" + task.getDescription() + "</li>\n");
			} else {
				writer.append("  <li>" + task.getDescription() + "</li>\n");
			}
		} else {
			if (renderState.openul) {
				writer.append("</ul>\n");
				renderState.openul = false;
			}
			renderState.inBlock = true;
			List<Short> priorities = task.priorities();
			System.out.println("##########" + priorities);
			String prioClasses;
			if (priorities.isEmpty()) {
				prioClasses = "";
			} else {
				prioClasses = priorities.stream().filter(s -> s.shortValue() != 0)
						.map(s -> "priority" + s.toString())
						.collect(Collectors.joining(" ", " class='", "'"));
			}
			if (task instanceof DirectoryTask) {
				writer.append("<h" + level + prioClasses + ">");
				writer.append("<a href='" + ((DirectoryTask) task).getPath() + "'>");
				writer.append(task.getDescription());
				writer.append("</a>");
				writer.append("</h" + level + ">\n");
			} else {
				writer.append("<h" + level + prioClasses + ">" + task.getDescription() + "</h" + level + ">\n");
			}
		}
	}

	private class RenderState {

		boolean openul = false;
		boolean inBlock = false;
	}
}
