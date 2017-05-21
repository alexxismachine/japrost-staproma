package de.japrost.staproma.renderer;

import java.io.IOException;
import java.io.Writer;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * Renderer to create HTML output from {@link Task}s.
 * 
 * @author alexxismachine (Ulrich David)
 */
public class StatusTaskHtmlRenderer {

	/**
	 * Do the rendering of the given task with its subtasks in status to the writer.
	 * 
	 * @param root the root task to start rendering from. MUST NOT be {@code null}.
	 * @param status the status tasks must be in for rendering.
	 * @param writer the writer to render to. MUST NOT be {@code null}.
	 * @throws IOException on io failures on the writer.
	 */
	public void render(Task root, TaskState status, Writer writer) throws IOException {
		//TODO writer and status as instance variables
		walkSubTree(root, 0, new RenderState(), status, writer);
	}

	private void walkSubTree(Task task, int level, RenderState renderState, TaskState status, Writer writer)
			throws IOException {
		int myLevel = level + 1;
		if (task.isInState(status)) {
			if (level > 0) {
				// do not print root task
				printTask(task, level, renderState, writer);
			}
			RenderState localRenderState = new RenderState();
			for (Task subTask : task) {
				walkSubTree(subTask, myLevel, localRenderState, status, writer);
			}
			if (localRenderState.openul) {
				writer.append("</ul>\n");
			}
		}
	}

	private void printTask(Task task, int level, RenderState renderState, Writer writer) throws IOException {
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
				writer.append("  <li class='priority"+task.getPriority()+"'>" + task.getDescription() + "</li>\n");
			} else {
				writer.append("  <li>" + task.getDescription() + "</li>\n");
			}
		} else {
			if (renderState.openul) {
				writer.append("</ul>\n");
				renderState.openul = false;
			}
			renderState.inBlock = true;
			if (task instanceof DirectoryTask) {
				writer.append("<h" + level + ">");
				writer.append("<a href='" + ((DirectoryTask) task).getPath() + "'>");
				writer.append(task.getDescription());
				writer.append("</a>");
				writer.append("</h" + level + ">\n");
			} else {
				writer.append("<h" + level + ">" + task.getDescription() + "</h" + level + ">\n");
			}
		}
	}

	private class RenderState {

		boolean openul = false;
		boolean inBlock = false;
	}
}
