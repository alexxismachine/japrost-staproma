package de.japrost.staproma;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.DirectoryWalker;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

import de.japrost.staproma.spm.DefaultSpmFactory;
import de.japrost.staproma.spm.SpmFormat;
import de.japrost.staproma.spm.SpmFormatFactory;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.Task;

/**
 * The TaksFileWalker scans the given directory for files and directories to convert them to a Task tree.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TaskFileWalker extends DirectoryWalker<String> {
	// TODO replace with logging
	private static final PrintStream OUT = System.out;
	private final File startDirectory;
	private final boolean filterCompleted;
	private final SpmFormatFactory spmFormatFactory;
	// TODO put this into the fileFilter! and use one pattern
	private static final Pattern MINMAL_DIR_PATTERN = Pattern.compile("^([A-Z]*)$");
	private static final Pattern NUMBER_DIR_PATTERN = Pattern.compile("^([A-Z]*)-(\\d*)$");
	private static final Pattern NAME_DIR_PATTERN = Pattern.compile("^([A-Z]*)_(.*$)");
	private static final Pattern FULL_DIR_PATTERN = Pattern.compile("^([A-Z]*)-(\\d*)_(.*$)");
	// (X*)
	// (X*)_(*)
	// (X*)-(N*)
	// (X*)-(N*)_(*)
	// => (X*)(-(N*))?(_(*))?
	// TODO put this into the fileFilter?
	// FIXME remove dependency between given IOFileFilter and this pattern
	private static final Pattern FILE_STATUS_PATTERN = Pattern.compile("^\\d*_?(.*)\\..*$");
	private int level = 0;
	private Task currentTask;

	/**
	 * Instanciate a with the given parameters. This instance will use the {@link DefaultSpmFactory} and filter
	 * completed tasks.
	 * 
	 * @param rootTask
	 *            the task to add the found tasks to.
	 * @param startDirectory
	 *            the base directory for the scan.
	 * @param fileFilter
	 *            the file filter to use.
	 */
	public TaskFileWalker(Task rootTask, File startDirectory, IOFileFilter fileFilter) {
		// TODO really needed?
		this(rootTask, startDirectory, fileFilter, new DefaultSpmFactory(), true);
	}

	/**
	 * Instanciate a with the given parameters.
	 * 
	 * @param rootTask
	 *            the task to add the found tasks to.
	 * @param startDirectory
	 *            the base directory for the scan.
	 * @param fileFilter
	 *            the file filter to use.
	 * @param spmFormatFactory
	 *            the factory to create {@link SpmFormat}s.
	 * @param filterCompleted
	 *            flag if completed tasks should be filtered.
	 */
	public TaskFileWalker(Task rootTask, File startDirectory, IOFileFilter fileFilter,
			SpmFormatFactory spmFormatFactory, boolean filterCompleted) {
		super(FileFilterUtils.directoryFileFilter(), fileFilter, -1);
		this.startDirectory = startDirectory;
		this.filterCompleted = filterCompleted;
		this.currentTask = rootTask;
		this.spmFormatFactory = spmFormatFactory;
	}

	/**
	 * Start the walk.
	 * 
	 * @throws IOException
	 *             on io errors.
	 */
	public void crawl() throws IOException {
		walk(startDirectory, null);
	}

	@Override
	protected boolean handleDirectory(File directory, int depth, Collection<String> results) throws IOException {
		OUT.println("Handle DIR :" + directory);
		if (!directory.equals(startDirectory)) {
			if (filterCompleted) {
				// FIXME set to status?
				if (containsCompleted(directory)) {
					OUT.println("-> Completed: " + directory.getAbsolutePath());
					return false;
				}
			}
			String dirName = directory.getName();
			String description = dirName;
			boolean match = false;
			// TODO faster
			Matcher matcher = MINMAL_DIR_PATTERN.matcher(dirName);
			if (matcher.matches()) {
				match = true;
				// keep description
			}
			matcher = NUMBER_DIR_PATTERN.matcher(dirName);
			if (matcher.matches()) {
				match = true;
				// keep descriptin
			}
			matcher = NAME_DIR_PATTERN.matcher(dirName);
			if (matcher.matches()) {
				match = true;
				//description = matcher.group(1) + " " + matcher.group(2).replace('_', ' ');
				description = matcher.group(2).replace('_', ' ');
			}
			matcher = FULL_DIR_PATTERN.matcher(dirName);
			if (matcher.matches()) {
				match = true;
				description = matcher.group(1) + "-" + matcher.group(2) + " " + matcher.group(3).replace('_', ' ');
			}
			if (match) {
				OUT.println("   DO THE STUFF and ignore results");
				String path = directory.getPath();
				String startDirPath = startDirectory.getPath();
				String relativePath = "." + path.substring(startDirPath.length());
				if (depth == level) {
					OUT.println("SAME");
					Task addTo = currentTask.getParent();
					DirectoryTask task = new DirectoryTask(addTo, relativePath, description);
					task.setState("FOLDER");
					addTo.addChild(task);
					currentTask = task;
				} else if (depth < level) {
					OUT.println("PARENT");
					Task addTo = currentTask.getParent().getParent();
					DirectoryTask task = new DirectoryTask(addTo, relativePath, description);
					task.setState("FOLDER");
					addTo.addChild(task);
					currentTask = task;
				} else { // depth > level
					OUT.println("SUB");
					Task addTo = currentTask;
					DirectoryTask task = new DirectoryTask(addTo, relativePath, description);
					task.setState("FOLDER");
					addTo.addChild(task);
					currentTask = task;
				}
				level = depth;
				return true;
			} else {
				OUT.println("-> Ignoring: " + directory.getAbsolutePath());
				return false;
			}
		} else {
			return true;
		}
	}

	@Override
	protected void handleFile(File file, int depth, Collection<String> results) throws IOException {
		OUT.println("Handle FILE:" + file);
		Matcher m = FILE_STATUS_PATTERN.matcher(file.getName());
		String status = "CURRENT";
		if (m.matches()) {
			status = m.group(1).toUpperCase();
			OUT.println(status);
		} else {
			OUT.println("This should not happen");
		}
		SpmFormat spmFormat = spmFormatFactory.construct(status);
		List<String> lines = readLines(file);
		spmFormat.parseLines(currentTask, lines);
	}

	// TODO handle with some interface?
	List<String> readLines(File file) throws IOException {
		return IOUtils.readLines(new FileInputStream(file));
	}

	@Override
	protected File[] filterDirectoryContents(File directory, int depth, File[] inFiles) throws IOException {
		OUT.println("filterDirectoryContents " + directory);
		List<File> files = new ArrayList<File>();
		List<File> dirs = new ArrayList<File>();
		// FIXME inFiles may be null!
		for (File f : inFiles) {
			if (f.isFile()) {
				files.add(f);
			} else {
				dirs.add(f);
			}
		}
		Collections.sort(files);
		Collections.sort(dirs);
		files.addAll(dirs);
		return files.toArray(new File[0]);
	}

	private boolean containsCompleted(File directory) {
		OUT.println("containsCompleted " + directory);
		IOFileFilter completedFilter = FileFilterUtils.and(FileFilterUtils.fileFileFilter(), new RegexFileFilter(
				"^\\d*_?completed\\.?.*$", IOCase.SENSITIVE));
		if (FileUtils.listFiles(directory, completedFilter, null).size() > 0) {
			return true;
		}
		return false;
	}
}
