package de.japrost.staproma.renderer;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;

import de.japrost.staproma.TaskStateFileName;

public class BaseRenderer {

	public void writeHead(final StringWriter writer, final String title) {
		writer.append("<!DOCTYPE html>\n");
		writer.append("<html>");
		writer.append("<head>");
		writer.append("<title>" + title + "</title>");
		writer.append("<link type='text/css' href='style.css' rel='stylesheet'/>");
		writer.append("<meta http-equiv='content-type' content='text/html; charset=UTF-8'/>");
		writer.append("<style>");
		for (int prio = 1; prio < 10; prio++) {
			writePriorityStyle(writer, prio);
		}
		includeStyle(writer);
		writer.append("</style>");
		writer.append("</head>");
		writer.append("<body>");
		writer.append("<div class='navigation'>");
		for (TaskStateFileName file : TaskStateFileName.values()) {
			writer.append("<a href='" + file.fileName() + "'>" + file.taskState() + "</a> ");
		}
		writer.append("</div>");
		writer.append("<hr/>");
		writer.append("<div class='title'>" + title + " Items</div>");
		writer.append("<hr/>");
		writer.append("<form>");
		for (int prio = 1; prio < 10; prio++) {
			writePriorityFrom(writer, prio);
		}
	}

	private void writePriorityFrom(final StringWriter writer, final int prio) {
		writer.append("<input type='checkbox' id='prio" + prio + "'" + (prio < 5 ? " checked" : "") + ">");
		writer.append("<label class='priority" + prio + "' for='prio" + prio + "'>Prio " + prio + "</label>");
	}

	private void writePriorityStyle(final StringWriter writer, final int prio) {
		writer.append(".priority" + prio + " {");
		writer.append("  max-height: 0;");
		writer.append("  overflow: hidden;");
		writer.append("}");
		for (int level = 2; level < 7; level++) {
			writer.append("#prio" + prio + ":checked ~h" + level + ".priority" + prio + " {");
			writer.append("  max-height: 100%;");
			writer.append("}");
		}
		writer.append("#prio" + prio + ":checked ~ul li.priority" + prio + " {");
		writer.append("  max-height: 100%;");
		writer.append("}");
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

	public void writeFoot(final StringWriter writer) {
		writer.append("<div class='generationTime'>" + new Date().toString() + "</div>");
		writer.append("</form>");
		writer.append("</body>");
		writer.append("</html>");
	}

}
