package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.Task;

/**
 * Second edition of RenderStaProMa.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class RenderStaProMa2 {
	private final File startDir;
	private final IOFileFilter fileFilter;

	/**
	 * Init.
	 * 
	 * @param startDir
	 *            base dir.
	 * @param fileFilter
	 *            filter which files to use.
	 */
	public RenderStaProMa2(File startDir, IOFileFilter fileFilter) {
		super();
		this.startDir = startDir;
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
		IOFileFilter currentFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), new RegexFileFilter(
				"^\\d*_?current\\..*$", IOCase.SENSITIVE));
		IOFileFilter futureFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), new RegexFileFilter(
				"^\\d*_?future\\..*$", IOCase.SENSITIVE));
		IOFileFilter doneFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), new RegexFileFilter(
				"^\\d*_?done\\..*$", IOCase.SENSITIVE));
		IOFileFilter gtdFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), new RegexFileFilter(
				"^\\d*_?gtd\\..*$", IOCase.SENSITIVE));
		IOFileFilter allFilter = FileFilterUtils.or(currentFilter, futureFilter, doneFilter, gtdFilter);
		File baseDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		if (args.length > 0) {
			baseDir = new File(args[0]);
		}
		RenderStaProMa2 current = new RenderStaProMa2(baseDir, allFilter);
		current.doAll();
	}

	private void doAll() throws IOException {
		Task root = crawlFiles();
		StatusTaskHtmlRenderer stw = new StatusTaskHtmlRenderer();
		writeFile(root, stw, "Current", "CURRENT", "01_currentItems.html");
		writeFile(root, stw, "Waiting", "WAITING", "02_waitingItems.html");
		writeFile(root, stw, "Scheduled", "SCHEDULE", "03_scheduledItems.html");
		writeFile(root, stw, "Future", "FUTURE", "10_futureItems.html");
		writeFile(root, stw, "Someday", "SOMEDAY", "20_somedayItems.html");
		writeFile(root, stw, "Done", "DONE", "99_doneItems.html");
		copyStyle();
	}

	private void writeFile(Task root, StatusTaskHtmlRenderer stw, String title, String status, String fileName)
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
		FileUtils.write(new File(startDir, fileName), content);
	}

	private void copyStyle() throws IOException {
		File srcCssFile = new File("src/main/resources/style.css");
		if (srcCssFile.canRead()) {
			File destCssFile = new File(startDir, "style.css");
			FileUtils.copyFile(srcCssFile, destCssFile);
		}
	}
}
