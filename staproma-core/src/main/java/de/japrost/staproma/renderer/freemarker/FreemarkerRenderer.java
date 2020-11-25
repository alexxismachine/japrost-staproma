package de.japrost.staproma.renderer.freemarker;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import de.japrost.staproma.TaskStateFileName;
import de.japrost.staproma.task.Task;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

/**
 * Renderer delegating to freemarker templates.
 */
public class FreemarkerRenderer {

	private final Writer writer;

	public FreemarkerRenderer(final Writer writer) {
		this.writer = writer;
	}

	public void render(final File templateDir, final Task rootTask, final String title,
			final TaskStateFileName taskStateFileName) throws IOException {

		Map<String, Object> root = new HashMap<>();
		root.put("tasks", rootTask);
		root.put("title", title);
		root.put("taskStateFileName", taskStateFileName);

		Configuration config = FreemarkerConfig.createConfiguration(templateDir);

		Template template = config.getTemplate(taskStateFileName.taskState().name() + ".ftlh");

		try {
			template.process(root, writer);
		} catch (TemplateException e) {
			throw new IOException(e);
		}
	}
}
