package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.time.LocalDate;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import de.japrost.staproma.renderer.ScheduledTaskHtmlRenderer;
import de.japrost.staproma.renderer.StatusTaskHtmlRenderer;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * Second edition of RenderStaProMa.
 *
 * @author alexxismachine (Ulrich David)
 */
public class RenderStaProMa2 {

	/** The base dir to read from. */
	private final File startDir;
	/** The base dir to write to. */
	private final File outDir;
	private final IOFileFilter fileFilter;
	private final String currentFileName = "01_currentItems.html";
	private final String waitingFileName = "02_waitingItems.html";
	private final String scheduledFileName = "03_scheduledItems.html";
	private final String futureFileName = "10_futureItems.html";
	private final String somedayFileName = "20_somedayItems.html";
	private final String doneFileName = "99_doneItems.html";

	/**
	 * Init using start dir also as out dir.
	 *
	 * @param startDir The base dir to read from.
	 * @param fileFilter filter which files to use.
	 */
	public RenderStaProMa2(final File startDir, final IOFileFilter fileFilter) {
		this(startDir, startDir, fileFilter);
	}

	/**
	 * Init.
	 *
	 * @param startDir The base dir to read from.
	 * @param outDir The base dir to write to.
	 * @param fileFilter filter which files to use.
	 */
	public RenderStaProMa2(final File startDir, final File outDir, final IOFileFilter fileFilter) {
		this.startDir = startDir;
		this.outDir = outDir;
		this.fileFilter = fileFilter;
		// FIXME use some logger
		boolean log = false;
		if (!log) {
			System.setOut(new PrintStream(new OutputStream() {

				@Override
				public void write(final int b) throws IOException {
					// NOOP
				}
			}));
		}
	}

	/**
	 * @param args command line args
	 * @throws IOException on io problems
	 */
	public static void main(final String[] args) throws IOException {
		final IOFileFilter currentFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?current\\..*$", IOCase.SENSITIVE));
		final IOFileFilter waitingFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?waiting\\..*$", IOCase.SENSITIVE));
		final IOFileFilter scheduleFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?schedule\\..*$", IOCase.SENSITIVE));
		final IOFileFilter futureFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?future\\..*$", IOCase.SENSITIVE));
		final IOFileFilter somedayFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?someday\\..*$", IOCase.SENSITIVE));
		final IOFileFilter doneFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?done\\..*$", IOCase.SENSITIVE));
		final IOFileFilter gtdFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?gtd\\..*$", IOCase.SENSITIVE));
		final IOFileFilter gtd2Filter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^([A-Z]+)-(\\d+)_(.*)\\.spm$", IOCase.SENSITIVE));
		final IOFileFilter allFilter = FileFilterUtils.or(currentFilter, waitingFilter, scheduleFilter, futureFilter,
				somedayFilter, doneFilter, gtdFilter, gtd2Filter);
		File baseDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		File outDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		if (args.length > 0) {
			baseDir = new File(args[0]);
			outDir = baseDir;
			System.err.println("Starting for " + baseDir.getAbsolutePath());
		}
		if (args.length > 1) {
			outDir = new File(args[1]);
			System.err.println("Writing to " + outDir.getAbsolutePath());
		}
		final RenderStaProMa2 current = new RenderStaProMa2(baseDir, outDir, allFilter);
		current.doAll();
	}

	private void doAll() throws IOException {
		final Task rootTask = crawlFiles();
		final StatusTaskHtmlRenderer stw = new StatusTaskHtmlRenderer();
		writeFile(rootTask, stw, "Current", TaskState.CURRENT, currentFileName);
		writeFile(rootTask, stw, "Waiting", TaskState.WAITING, waitingFileName);
		// special handling for scheduled tasks
		// add a line for today
		final LeafTask today = new LeafTask(rootTask,
				LocalDate.now().toString() + " +-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+");
		today.setState(TaskState.SCHEDULE);
		rootTask.addChild(today);
		// special rendering
		writeScheduleFile(rootTask, "Scheduled", scheduledFileName);
		writeFile(rootTask, stw, "Future", TaskState.FUTURE, futureFileName);
		writeFile(rootTask, stw, "Someday", TaskState.SOMEDAY, somedayFileName);
		writeFile(rootTask, stw, "Done", TaskState.DONE, doneFileName);
		copyStyle();
	}

	private void writeScheduleFile(final Task root, final String title, final String fileName) throws IOException {
		StringWriter writer;
		writer = new StringWriter();
		writeHead(writer, title);
		final ScheduledTaskHtmlRenderer scheduledTaskHtmlRenderer = new ScheduledTaskHtmlRenderer(root, writer);
		scheduledTaskHtmlRenderer.render();
		writeFoot(writer);

		writeFile(fileName, writer.toString());
	}

	private void writeFile(final Task root, final StatusTaskHtmlRenderer stw, final String title, final TaskState status,
			final String fileName) throws IOException {
		StringWriter writer;
		writer = new StringWriter() {

			@Override
			public StringWriter append(final CharSequence csq) {
				write(String.valueOf(csq));
				write("\n");
				return this;
			}
		};
		writeHead(writer, title);
		stw.render(root, status, writer);
		writeFoot(writer);
		writeFile(fileName, writer.toString());
	}

	private void writeHead(final StringWriter writer, final String title) {
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
		writer.append("</style>");
		writer.append("</head>");
		writer.append("<body>");
		writer.append("<div class='navigation'>");
		writer.append("<a href='" + currentFileName + "'>Current</a> ");
		writer.append("<a href='" + waitingFileName + "'>Waiting</a> ");
		writer.append("<a href='" + scheduledFileName + "'>Scheduled</a> ");
		writer.append("<a href='" + futureFileName + "'>Future</a> ");
		writer.append("<a href='" + somedayFileName + "'>Someday</a> ");
		writer.append("<a href='" + doneFileName + "'>Done</a> ");
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
		writer.append("#prio" + prio + ":checked ~h2.priority" + prio + " {");
		writer.append("  max-height: 100%;");
		writer.append("}");
		writer.append("#prio" + prio + ":checked ~h3.priority" + prio + " {");
		writer.append("  max-height: 100%;");
		writer.append("}");
		writer.append("#prio" + prio + ":checked ~ul li.priority" + prio + " {");
		writer.append("  max-height: 100%;");
		writer.append("}");
	}

	private void writeFoot(final StringWriter writer) {
		writer.append("<div class='generationTime'>" + new Date().toString() + "</div>");
		writer.append("</form>");
		writer.append("</body>");
		writer.append("</html>");
	}

	private Task crawlFiles() throws IOException {
		final Task root = new FolderTask(null, "ROOT");
		final TaskFileWalker fileWalker = new TaskFileWalker(root, startDir, fileFilter);
		fileWalker.crawl();
		return root;
	}

	private void writeFile(final String fileName, final String content) throws IOException {
		FileUtils.write(new File(outDir, fileName), content);
	}

	private void copyStyle() throws IOException {
		final File srcCssFile = new File("src/main/resources/style.css");
		if (srcCssFile.canRead()) {
			final File destCssFile = new File(outDir, "style.css");
			FileUtils.copyFile(srcCssFile, destCssFile);
		}
	}
}
