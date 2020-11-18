package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.time.LocalDate;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;

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
		FileFilters fileFilters = new FileFilters();
		final IOFileFilter allFilter = FileFilterUtils.or(fileFilters.currentFilter, fileFilters.waitingFilter,
				fileFilters.scheduleFilter,
				fileFilters.futureFilter,
				fileFilters.somedayFilter, fileFilters.doneFilter, fileFilters.gtdFilter, fileFilters.gtd2Filter);
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
		FileWriter fileWriter = new FileWriter(outDir);
		final Task rootTask = crawlFiles();
		fileWriter.writeFile(rootTask, "Current", TaskStateFileName.CURRENT);
		fileWriter.writeFile(rootTask, "Waiting", TaskStateFileName.WAITING);
		// special handling for scheduled tasks
		// add a line for today
		final LeafTask today = new LeafTask(rootTask,
				LocalDate.now().toString() + " +-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+-_-+");
		today.setState(TaskState.SCHEDULE);
		rootTask.addChild(today);
		// special rendering
		fileWriter.writeScheduleFile(rootTask, "Scheduled", TaskStateFileName.SCHEDULE);
		fileWriter.writeFile(rootTask, "Future", TaskStateFileName.FUTURE);
		fileWriter.writeFile(rootTask, "Someday", TaskStateFileName.SOMEDAY);
		fileWriter.writeFile(rootTask, "Done", TaskStateFileName.DONE);
		StyleCopy sc = new StyleCopy(outDir);
		sc.copyStyle();
	}

	private Task crawlFiles() throws IOException {
		final Task root = new FolderTask(null, "ROOT");
		final TaskFileWalker fileWalker = new TaskFileWalker(root, startDir, fileFilter);
		fileWalker.crawl();
		return root;
	}

}
