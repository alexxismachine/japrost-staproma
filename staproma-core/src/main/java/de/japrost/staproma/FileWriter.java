package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;

import de.japrost.staproma.renderer.BaseRenderer;
import de.japrost.staproma.renderer.ScheduledTaskHtmlRenderer;
import de.japrost.staproma.renderer.StatusTaskHtmlRenderer;
import de.japrost.staproma.renderer.freemarker.FreemarkerRenderer;
import de.japrost.staproma.task.Task;

public class FileWriter {

	/** The base dir to write to. */
	private final File outDir;
	private final File templateDir;

	FileWriter(final File outDir, final File templateDir) {
		this.outDir = outDir;
		this.templateDir = templateDir;
	}

	void writeFile(final Task root, final String title,
			final TaskStateFileName taskStateFileName) throws IOException {
		StringWriter writer = new NewLineStringWriter();
		BaseRenderer br = new BaseRenderer();
		br.writeHead(writer, title);
		StatusTaskHtmlRenderer renderer = new StatusTaskHtmlRenderer(root, taskStateFileName.taskState(), writer);
		renderer.render();

		br.writeFoot(writer);
		writeFile(taskStateFileName.fileName(), writer.toString());

		StringWriter writer2 = new NewLineStringWriter();
		FreemarkerRenderer freemarkerRenderer = new FreemarkerRenderer(writer2, "baseGtd.ftlh");
		freemarkerRenderer.render(templateDir, root, title, taskStateFileName);
		writeFile("ftl-" + taskStateFileName.fileName(), writer2.toString());

	}

	void writeScheduleFile(final Task root, final String title, final TaskStateFileName taskStateFileName)
			throws IOException {
		StringWriter writer = new NewLineStringWriter();
		BaseRenderer br = new BaseRenderer();
		br.writeHead(writer, title);

		final ScheduledTaskHtmlRenderer renderer = new ScheduledTaskHtmlRenderer(root, writer);
		renderer.render();

		br.writeFoot(writer);
		writeFile(taskStateFileName.fileName(), writer.toString());

		StringWriter writer2 = new NewLineStringWriter();
		FreemarkerRenderer freemarkerRenderer = new FreemarkerRenderer(writer2, "baseSchedule.ftlh");
		freemarkerRenderer.render(templateDir, root, title, taskStateFileName);
		writeFile("ftl-" + taskStateFileName.fileName(), writer2.toString());
	}

	private void writeFile(final String fileName, final String content) throws IOException {
		File file = new File(outDir, fileName);
		System.err.println("Writing " + file.getAbsolutePath());
		FileUtils.write(file, content);
	}

	private void includeStyle(final StringWriter writer) {
		InputStream in = this.getClass().getResourceAsStream("/style.css");
		if (in != null) {
			try {
				writer.append(new String(in.readAllBytes()));
			} catch (IOException e) {
				writer.append("<!-- style.css not readable -->");
			}
		} else {
			writer.append("<!-- style.css not found -->");
		}
	}

}
