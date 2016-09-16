package de.japrost.staproma;

import static de.japrost.staproma.TaskState.CURRENT;

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
 * 
 */
public class RenderStaProMa2 {
	/** The base dir to read from. */
	private final File startDir;
	/** The base dir to write to. */
	private final File outDir;
	private final IOFileFilter fileFilter;
	private String currentFileName = "01_currentItems.html";
	private String waitingFileName = "02_waitingItems.html";
	private String scheduledFileName = "03_scheduledItems.html";
	private String futureFileName = "10_futureItems.html";
	private String somedayFileName = "20_somedayItems.html";
	private String doneFileName = "99_doneItems.html";

	/**
	 * Init using start dir also as out dir.
	 * 
	 * @param startDir
	 *            The base dir to read from.
	 * @param fileFilter
	 *            filter which files to use.
	 */
	public RenderStaProMa2(File startDir, IOFileFilter fileFilter) {
		this(startDir, startDir, fileFilter);
	}

	/**
	 * Init.
	 * 
	 * @param startDir
	 *            The base dir to read from.
	 * @param outDir
	 *            The base dir to write to.
	 * @param fileFilter
	 *            filter which files to use.
	 */
	public RenderStaProMa2(File startDir, File outDir, IOFileFilter fileFilter) {
		this.startDir = startDir;
		this.outDir = outDir;
		this.fileFilter = fileFilter;
		// FIXME use some logger
		System.setOut(new PrintStream(new OutputStream() {
			@Override
			public void write(int b) throws IOException {
				// NOOP
			}
		}));
	}

	/**
	 * @param args
	 *            command line args
	 * @throws IOException
	 *             on io problems
	 */
	public static void main(String[] args) throws IOException {
		IOFileFilter currentFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?current\\..*$", IOCase.SENSITIVE));
		IOFileFilter futureFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?future\\..*$", IOCase.SENSITIVE));
		IOFileFilter doneFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?done\\..*$", IOCase.SENSITIVE));
		IOFileFilter gtdFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter("^\\d*_?gtd\\..*$", IOCase.SENSITIVE));
		IOFileFilter allFilter = FileFilterUtils.or(currentFilter, futureFilter, doneFilter, gtdFilter);
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
		RenderStaProMa2 current = new RenderStaProMa2(baseDir, outDir, allFilter);
		current.doAll();
	}

	private void doAll() throws IOException {
		Task rootTask = crawlFiles();
		StatusTaskHtmlRenderer stw = new StatusTaskHtmlRenderer();
		writeFile(rootTask, stw, "Current", TaskState.CURRENT, currentFileName);
		writeFile(rootTask, stw, "Waiting", TaskState.WAITING, waitingFileName);
		// special handling for scheduled tasks
		// add a line for today
		LeafTask today = new LeafTask(rootTask,
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

	private void writeScheduleFile(Task root, String title, String fileName) throws IOException {
		StringWriter writer;
		writer = new StringWriter();
		writeHead(writer, title);
		ScheduledTaskHtmlRenderer scheduledTaskHtmlRenderer = new ScheduledTaskHtmlRenderer(root, writer);
		scheduledTaskHtmlRenderer.render();
		writeFoot(writer);

		writeFile(fileName, writer.toString());
	}

	private void writeFile(Task root, StatusTaskHtmlRenderer stw, String title, TaskState status, String fileName)
			throws IOException {
		StringWriter writer;
		writer = new StringWriter();
		writeHead(writer, title);
		stw.render(root, status, writer);
		writeFoot(writer);
		writeFile(fileName, writer.toString());
	}

	private void writeHead(StringWriter writer, String title) {
		writer.append("<!DOCTYPE html>\n");
		writer.append("<html>");
		writer.append("<head>");
		writer.append("<title>" + title + "</title>");
		writer.append("<link type='text/css' href='style.css' rel='stylesheet'/>");
		writer.append("<meta http-equiv='content-type' content='text/html; charset=UTF-8'/>");
		writer.append("</head>");
		writer.append("<body>");
		writer.append("<div class='navigation'></div>");
		writer.append("<a href='" + currentFileName + "'>Current</a> ");
		writer.append("<a href='" + waitingFileName + "'>Waiting</a> ");
		writer.append("<a href='" + scheduledFileName + "'>Scheduled</a> ");
		writer.append("<a href='" + futureFileName + "'>Future</a> ");
		writer.append("<a href='" + somedayFileName + "'>Someday</a> ");
		writer.append("<a href='" + doneFileName + "'>Done</a> ");
		writer.append("</div>");
		writer.append("<hr/>");
		writer.append("<div class='titel'>" + title + " Items</div>");
		writer.append("<hr/>");
	}

	private void writeFoot(StringWriter writer) {
		writer.append(new Date().toString());
		writer.append("</body>");
		writer.append("</html>");
	}

	private Task crawlFiles() throws IOException {
		Task root = new FolderTask(null, "ROOT");
		TaskFileWalker fileWalker = new TaskFileWalker(root, startDir, fileFilter);
		fileWalker.crawl();
		return root;
	}

	private void writeFile(String fileName, String content) throws IOException {
		FileUtils.write(new File(outDir, fileName), content);
	}

	private void copyStyle() throws IOException {
		File srcCssFile = new File("src/main/resources/style.css");
		if (srcCssFile.canRead()) {
			File destCssFile = new File(outDir, "style.css");
			FileUtils.copyFile(srcCssFile, destCssFile);
		}
	}
}
