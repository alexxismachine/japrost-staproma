package de.japrost.staproma.renderer;

import static org.junit.Assert.*;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.AnonymousTask;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.LeafTask;
import de.jplanets.junit.AssertString;

/**
 * Test the {@link ScheduledTaskHtmlRenderer}.
 */
public class ScheduledTaskHtmlRendererTest {
	private ScheduledTaskHtmlRenderer cut;

	private List<String> expecteds;
	
	@Before
	public void setUp() {
		expecteds = new ArrayList<>();
	}
	/**
	 * Test that constructor fails fast, if null is passed in.
	 */
	@Test
	public void constructorFailsFastOnNull() {
		try {
			cut = new ScheduledTaskHtmlRenderer(null, null);
			fail("Should not be instanciable with null");
		} catch (IllegalArgumentException e) {
			// this should happen
		}
		try {
			cut = new ScheduledTaskHtmlRenderer(new AnonymousTask(null), null);
			fail("Should not be instanciable with null");
		} catch (IllegalArgumentException e) {
			// this should happen
		}
		try {
			cut = new ScheduledTaskHtmlRenderer(null, new StringWriter());
			fail("Should not be instanciable with null");
		} catch (IllegalArgumentException e) {
			// this should happen
		}
	}

	/**
	 * Test the basic rendering with one task.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void basicFormat() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		LeafTask rootTask = new LeafTask(null, "2016-01-01 Do some important stuff");
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		// then
		expecteds.add("<dl>");
		addDtDd("2016-01-01", "Do some important stuff");
		expecteds.add("</dl>");
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}

	/**
	 * Test the basic rendering with multiple tasks. Root heading is ignored.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void basicFormatMultipleTasks() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		FolderTask rootTask = new FolderTask(null, "Some Heading");
		LeafTask firstTask = new LeafTask(rootTask, "2016-01-01 Do some important stuff");
		firstTask.setState(TaskState.SCHEDULE);
		rootTask.addChild(firstTask);
		LeafTask secondTask = new LeafTask(rootTask, "2016-02-01 Do more stuff");
		secondTask.setState(TaskState.SCHEDULE);
		rootTask.addChild(secondTask);
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		// then
		twoTaskResult();
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}

	/**
	 * Test the sort multiple tasks by date.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void sortMultipleTasks() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		FolderTask rootTask = new FolderTask(null, "Some Heading");
		LeafTask secondTask = new LeafTask(rootTask, "2016-02-01 Do more stuff");
		secondTask.setState(TaskState.SCHEDULE);
		rootTask.addChild(secondTask);
		LeafTask firstTask = new LeafTask(rootTask, "2016-01-01 Do some important stuff");
		firstTask.setState(TaskState.SCHEDULE);
		rootTask.addChild(firstTask);
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		twoTaskResult();
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}

	/**
	 * Test the sort multiple tasks by date not minding the level they are on.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void sortMultipleTasksOnLevels() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		FolderTask rootTask = new FolderTask(null, "Some Heading");
		LeafTask firstTask = new LeafTask(rootTask, "2016-01-01 Do some important stuff");
		firstTask.setState(TaskState.SCHEDULE);
		// second on different level
		FolderTask folder = new FolderTask(rootTask, "Intermediate");
		LeafTask secondTask = new LeafTask(folder, "2016-02-01 Do more stuff");
		secondTask.setState(TaskState.SCHEDULE);
		folder.addChild(secondTask);
		rootTask.addChild(folder);
		// add first after intermediate
		rootTask.addChild(firstTask);
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		// then
		expecteds.add("<dl>");
		firstTaskResult();
		addDtDd("2016-02-01", "Do more stuff : Intermediate");
		expecteds.add("</dl>");
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}
	/**
	 * Test create a link for directory tasks.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void createLinkForDirectoryTasks() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		FolderTask rootTask = new FolderTask(null, "Some Heading");
		LeafTask firstTask = new LeafTask(rootTask, "2016-01-01 Do some important stuff");
		firstTask.setState(TaskState.SCHEDULE);
		// second on different level
		DirectoryTask folder = new DirectoryTask(rootTask,"./path", "Intermediate");
		LeafTask secondTask = new LeafTask(folder, "2016-02-01 Do more stuff");
		secondTask.setState(TaskState.SCHEDULE);
		folder.addChild(secondTask);
		rootTask.addChild(folder);
		// add first after intermediate
		rootTask.addChild(firstTask);
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		// then
		expecteds.add("<dl>");
		firstTaskResult();
		addDtDd("2016-02-01", "Do more stuff : <a href='./path'>Intermediate</a>");
		expecteds.add("</dl>");
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}

	/**
	 * Test that non scheduled tasks are ignored.
	 * 
	 * @throws Exception
	 *             never
	 */
	@Test
	public void ignoreNonScheduledTasks() throws Exception {
		// given
		StringWriter writer = new StringWriter();
		FolderTask rootTask = new FolderTask(null, "Some Heading");
		LeafTask firstTask = new LeafTask(rootTask, "2016-01-01 Do some important stuff");
		firstTask.setState(TaskState.SCHEDULE);
		rootTask.addChild(firstTask);
		LeafTask secondTask = new LeafTask(rootTask, "Do more stuff now!");
		secondTask.setState(TaskState.CURRENT);
		rootTask.addChild(secondTask);
		cut = new ScheduledTaskHtmlRenderer(rootTask, writer);
		// when
		cut.render();
		// then
		expecteds.add("<dl>");
		firstTaskResult();
		expecteds.add("</dl>");
		AssertString.assertLinesEqual(expecteds.toArray(new String[0]), writer.toString().split("\n"));
	}

	private void twoTaskResult() {
		expecteds.add("<dl>");
		firstTaskResult();
		secondTaskResult();
		expecteds.add("</dl>");
	}

	private void firstTaskResult() {
		addDtDd("2016-01-01", "Do some important stuff");
	}
	private void secondTaskResult() {
		addDtDd("2016-02-01", "Do more stuff");
	}

	private void addDtDd(String dt, String dd) {
		expecteds.add("  <dt>"+dt+"</dt>");
		expecteds.add("  <dd>"+dd+"</dd>");
		expecteds.add("");
	}

}
