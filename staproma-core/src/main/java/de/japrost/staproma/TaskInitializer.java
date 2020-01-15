package de.japrost.staproma;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Tool to initalize a new Task
 */
public class TaskInitializer {

	private final String baseDir;
	private final String refMatDir;
	private final String templateDir;
	private final String templateName;
	private final String projectDir;
	private final String taskName;

	/**
	 * Start the tool
	 *
	 * @param args the commandline arguments
	 * @throws IOException
	 */
	public static void main(final String[] args) throws IOException {
		TaskInitializer main = new TaskInitializer(args[0], args[1], args[2], args[3], args[4], args[5]);
		main.doInitialize();
	}

	public TaskInitializer(final String baseDir, final String refMatDir, final String templateDir,
			final String templateName, final String projectDir, final String taskName) {
		this.baseDir = baseDir;
		this.refMatDir = refMatDir;
		this.templateDir = templateDir;
		this.templateName = templateName;
		this.projectDir = projectDir;
		this.taskName = taskName;
	}

	private void doInitialize() throws IOException {
		// copy template to project dir renaming it to taskName.spm
		String relProPath = projectDir.substring(baseDir.length());
		//System.out.println(relProPath);
		Path templatePath = Paths.get(templateDir, templateName + ".spm");
		Path taskPath = Paths.get(baseDir + "/" + relProPath, taskName + ".spm");
		Files.copy(templatePath, taskPath);
		// create dir refMatDir/projectDir/taskName
		String refMatPath = refMatDir + "/" + relProPath + "/" + taskName;
		//System.out.println(refMatPath);
		File refDir = new File(refMatPath);
		refDir.mkdirs();
	}

}
