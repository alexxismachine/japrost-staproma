package de.japrost.staproma;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.LeafTask;
import de.japrost.staproma.task.Task;

/**
 * Test the {@link StatusTaskHtmlRenderer}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestStatusTaskHtmlRenderer {
	private StatusTaskHtmlRenderer cut;
	private Writer writer;
	private Task root;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new StatusTaskHtmlRenderer();
		root = new FolderTask(null, "ROOT");
		writer = new StringWriter();
	}

	/**
	 * Test rendering empty data creates empty result.
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderEmptyData() throws IOException {
		cut.render(root, null, writer);
		Assert.assertEquals("", writer.toString());
	}

	/**
	 * Test rendering first level.
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderFirstLevel() throws IOException {
		root.addChild(new FolderTask(root, "subTask"));
		cut.render(root, null, writer);
		Assert.assertEquals("<h1>subTask</h1>\n", writer.toString());
	}

	/**
	 * Test rendering two levels.
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderTwoLevels() throws IOException {
		FolderTask subTask = new FolderTask(root, "subTask");
		root.addChild(subTask);
		FolderTask subSubTask = new FolderTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		cut.render(root, null, writer);
		Assert.assertEquals("<h1>subTask</h1>\n<h2>subSubTask</h2>\n", writer.toString());
	}

	/**
	 * Test rendering LeafTask
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderLeafTask() throws IOException {
		Task subTask = new FolderTask(root, "subTask");
		root.addChild(subTask);
		Task subSubTask = new LeafTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		String status = null;
		cut.render(root, status, writer);
		Assert.assertEquals("<h1>subTask</h1>\n<ul>\n  <li>subSubTask</li>\n</ul>\n", writer.toString());
	}

	/**
	 * Test rendering LeafTask in DirectoryTask
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderLeafTaskInDirectoryTask() throws IOException {
		Task subTask = new DirectoryTask(root, "path", "subTask");
		root.addChild(subTask);
		Task subSubTask = new LeafTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		String status = null;
		cut.render(root, status, writer);
		Assert.assertEquals("<h1><a href='path'>subTask</a></h1>\n<ul>\n  <li>subSubTask</li>\n</ul>\n",
				writer.toString());
	}

	/**
	 * Test rendering multiple LeafTasks
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderMultipleLeafTasks() throws IOException {
		Task subTask = new DirectoryTask(root, "path", "subTask");
		root.addChild(subTask);
		Task subSubTask = new LeafTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		Task subSubTask2 = new LeafTask(root, "subSubTask2");
		subTask.addChild(subSubTask2);
		String status = null;
		cut.render(root, status, writer);
		Assert.assertEquals(
				"<h1><a href='path'>subTask</a></h1>\n<ul>\n  <li>subSubTask</li>\n  <li>subSubTask2</li>\n</ul>\n",
				writer.toString());
	}

	/**
	 * Test rendering LeafTask after FolderTask building an intermediate heading.
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderLeafTaskAfterFolderTask() throws IOException {
		Task subTask = new DirectoryTask(root, "path", "subTask");
		root.addChild(subTask);
		Task subSubTask = new FolderTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		Task subSubTask2 = new LeafTask(root, "subSubTask2");
		subTask.addChild(subSubTask2);
		String status = null;
		cut.render(root, status, writer);
		Assert.assertEquals(
				"<h1><a href='path'>subTask</a></h1>\n<h2>subSubTask</h2>\n<h2>WEITERES</h2>\n<ul>\n  <li>subSubTask2</li>\n</ul>\n",
				writer.toString());
	}

	/**
	 * Test rendering autoclose LeafTask.
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderAutocloseLeafTask() throws IOException {
		Task subTask = new DirectoryTask(root, "path", "subTask");
		root.addChild(subTask);
		Task subSubTask = new LeafTask(root, "subSubTask");
		subTask.addChild(subSubTask);
		Task subSubTask2 = new FolderTask(root, "subSubTask2");
		subTask.addChild(subSubTask2);
		String status = null;
		cut.render(root, status, writer);
		Assert.assertEquals(
				"<h1><a href='path'>subTask</a></h1>\n<ul>\n  <li>subSubTask</li>\n</ul>\n<h2>subSubTask2</h2>\n",
				writer.toString());
	}

	/**
	 * Test rendering ...
	 * 
	 * @throws IOException
	 *             on io failure (never in test).
	 */
	@Test
	public void renderIgnoresWrongState() throws IOException {
		root.addChild(new FolderTask(root, "subTask"));
		String status = "OK";
		cut.render(root, status, writer);
		Assert.assertEquals("", writer.toString());
	}
}
