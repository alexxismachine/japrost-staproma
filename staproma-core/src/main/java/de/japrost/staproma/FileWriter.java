package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

import org.apache.commons.io.FileUtils;

import de.japrost.staproma.renderer.BaseRenderer;
import de.japrost.staproma.renderer.ScheduledTaskHtmlRenderer;
import de.japrost.staproma.renderer.StatusTaskHtmlRenderer;
import de.japrost.staproma.task.Task;

public class FileWriter {

	/** The base dir to write to. */
	private final File outDir;

	FileWriter(final File outDir) {
		this.outDir = outDir;
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
	}

	void writeScheduleFile(final Task root, final String title, final TaskStateFileName fileName) throws IOException {
		StringWriter writer = new NewLineStringWriter();
		BaseRenderer br = new BaseRenderer();
		br.writeHead(writer, title);

		final ScheduledTaskHtmlRenderer renderer = new ScheduledTaskHtmlRenderer(root, writer);
		renderer.render();

		br.writeFoot(writer);
		writeFile(fileName.fileName(), writer.toString());
	}

	private void writeFile(final String fileName, final String content) throws IOException {
		FileUtils.write(new File(outDir, fileName), content);
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