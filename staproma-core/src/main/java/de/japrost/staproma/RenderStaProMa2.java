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
	/** The dir to look for templates. */
	private final File templateDir;
	private final IOFileFilter fileFilter;

	/**
	 * Init using start dir also as out dir.
	 *
	 * @param startDir The base dir to read from.
	 * @param fileFilter filter which files to use.
	 */
	public RenderStaProMa2(final File startDir, final IOFileFilter fileFilter) {
		this(startDir, startDir, startDir, fileFilter);
	}

	/**
	 * Init using IoDirs.
	 *
	 * @param dirs The dirs to read from / write to.
	 * @param fileFilter filter which files to use.
	 */
	private RenderStaProMa2(final IoDirs dirs, final IOFileFilter fileFilter) {
		this(dirs.startDir, dirs.outDir, dirs.templateDir, fileFilter);
	}

	/**
	 * Init.
	 *
	 * @param startDir The base dir to read from.
	 * @param outDir The base dir to write to.
	 * @param fileFilter filter which files to use.
	 */
	public RenderStaProMa2(final File startDir, final File outDir, final File templateDir,
			final IOFileFilter fileFilter) {
		this.startDir = startDir;
		this.outDir = outDir;
		this.templateDir = templateDir;
		this.fileFilter = fileFilter;
		// FIXME use some logger
		boolean log = true;
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
		final IOFileFilter allFilter = setupFilters();
		final IoDirs dirs = setupIoDirs(args);
		final RenderStaProMa2 current = new RenderStaProMa2(dirs, allFilter);
		current.doAll();
	}

	private static IoDirs setupIoDirs(final String[] args) {
		IoDirs dirs = new IoDirs();
		dirs.startDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		dirs.templateDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		dirs.outDir = new File("/home/uli/media/DSOne_home/01_ToDo/");
		if (args.length > 0) {
			dirs.startDir = new File(args[0]);
			dirs.templateDir = dirs.startDir;
			dirs.outDir = dirs.startDir;
			System.err.println("Starting for " + dirs.startDir.getAbsolutePath());
		}
		if (args.length > 1) {
			dirs.outDir = new File(args[1]);
			System.err.println("Writing to " + dirs.outDir.getAbsolutePath());
		}
		if (args.length > 2) {
			dirs.templateDir = new File(args[2]);
			System.err.println("Templates from " + dirs.templateDir.getAbsolutePath());
		}
		return dirs;
	}

	private static IOFileFilter setupFilters() {
		FileFilters fileFilters = new FileFilters();
		final IOFileFilter allFilter = FileFilterUtils.or(fileFilters.currentFilter, fileFilters.waitingFilter,
				fileFilters.scheduleFilter,
				fileFilters.futureFilter,
				fileFilters.somedayFilter, fileFilters.doneFilter, fileFilters.gtdFilter, fileFilters.gtd2Filter);
		return allFilter;
	}

	private void doAll() throws IOException {
		final Task rootTask = crawlFiles();
		writeFiles(rootTask);
		StyleCopy sc = new StyleCopy(outDir);
		sc.copyStyle();
	}

	private void writeFiles(final Task rootTask) throws IOException {
		FileWriter fileWriter = new FileWriter(outDir, templateDir);
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
	}

	private Task crawlFiles() throws IOException {
		final Task root = new FolderTask(null, "ROOT");
		final TaskFileWalker fileWalker = new TaskFileWalker(root, startDir, fileFilter);
		fileWalker.crawl();
		return root;
	}

	/**
	 * Thin interanal wrapper for dirs to read from / write to.
	 */
	private static class IoDirs {

		private File startDir;
		private File outDir;
		private File templateDir;

	}
}
