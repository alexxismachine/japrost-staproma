package de.japrost.staproma.renderer.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.japrost.staproma.TaskStateFileName;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Renderer delegating to freemarker templates.
 */
public class FreemarkerRenderer {

	private final Writer writer;
	private final String templateName;

	public FreemarkerRenderer(final Writer writer, final String templateName) {
		this.writer = writer;
		this.templateName = templateName;
	}

	public void render(final File templateDir, final Task rootTask, final String title,
			final TaskStateFileName taskStateFileName) throws IOException {

		Map<String, Object> root = new HashMap<>();
		root.put("tasks", rootTask);
		root.put("title", title);
		root.put("taskStateFileName", taskStateFileName);
		root.put("leafsFlat", flatLeafs(rootTask));

		Configuration config = FreemarkerConfig.createConfiguration(templateDir);

		Template template = config.getTemplate(templateName);

		try {
			template.process(root, writer);
		} catch (TemplateException e) {
			throw new IOException(e);
		}
	}

	private List<Task> flatLeafs(final Task rootTask) {
		List<Task> leafs = new ArrayList<>();
		for (Task subTask : rootTask) {
			if (subTask instanceof LeafTask) {
				leafs.add(subTask);
			} else {
				leafs.addAll(flatLeafs(subTask));
			}
		}
		return leafs;
	}
}
