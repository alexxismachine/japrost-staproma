package de.japrost.staproma;

import static org.easymock.EasyMock.anyObject;
import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import de.japrost.staproma.spm.SpmFormat;
import de.japrost.staproma.spm.SpmFormatFactory;
import de.japrost.staproma.task.DirectoryTask;
import de.japrost.staproma.task.FolderTask;
import de.japrost.staproma.task.Task;

/**
 * Test the {@link TaskFileWalker}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestTaskFileWalker {
	private static final String TEST_BASE_DIR = "target/test-classes";
	private TaskFileWalker cut;
	private EasyMockSupport ems;
	private Task rootTask;
	private IOFileFilter ioFileFilter;
	private SpmFormatFactory spmFormatFactoryMock;
	private SpmFormat spmFormatMock;

	/**
	 * Setup whole test
	 */
	@BeforeClass
	public static void init() {
		// set up empty dir structure
		new File(TEST_BASE_DIR,"completedBaseDir/ACTIVE").mkdirs();
		new File(TEST_BASE_DIR,"completedBaseDir/LAST").mkdirs();
		new File(TEST_BASE_DIR,"emptyBaseDir").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/FULL-15_withName").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/IGNORED-DIR").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/NUMBERDIR-42").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/MINIMAL").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/NAMEDIR_theName").mkdirs();
		new File(TEST_BASE_DIR,"patternBaseDir/NAMEDIR_theName").mkdirs();
		new File(TEST_BASE_DIR,"treeBaseDir/SUB-1/SUB-1_SUB-1").mkdirs();
		new File(TEST_BASE_DIR,"treeBaseDir/SUB-2").mkdirs();
	}
	
	/**
	 * Set up each test.
	 * 
	 */
	@Before
	public void setUp() {
		ems = new EasyMockSupport();
		rootTask = new FolderTask(null, "ROOT");
		spmFormatFactoryMock = ems.createMock(SpmFormatFactory.class);
		spmFormatMock = ems.createMock(SpmFormat.class);
		ioFileFilter = FileFilterUtils.fileFileFilter();
	}

	private TaskFileWalker setUpWithBaseDir(File baseDir) {
		baseDir.mkdirs();
		System.out.println(baseDir.getAbsolutePath());
		return new TaskFileWalker(rootTask, baseDir, ioFileFilter, spmFormatFactoryMock, true);
	}

	private TaskFileWalker setUpWithBaseDirNotFilter(File baseDir) {
		return new TaskFileWalker(rootTask, baseDir, ioFileFilter, spmFormatFactoryMock, false);
	}

	/**
	 * Empty dir creates emtpty ROOT.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@Test
	public void handleEmptyDir() throws IOException {
		cut = setUpWithBaseDir(new File(TEST_BASE_DIR, "emptyBaseDir"));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		assertFalse(rootTask.hasChildren());
	}

	/**
	 * Test handling all the dir patterns.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	// TODO use separate tests for each pattern?
	@Test
	public void handleDirPatterns() throws IOException {
		cut = setUpWithBaseDir(new File(TEST_BASE_DIR, "patternBaseDir"));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		assertTrue(rootTask.hasChildren());
		Iterator<Task> iterator = rootTask.iterator();
		Task next;
		//
		next = iterator.next();
		assertEquals("FULL-15 withName", next.getDescription());
		assertTrue(next.isInState(TaskState.CURRENT));
		assertEquals(rootTask, next.getParent());
		assertFalse(next.hasChildren());
		//
		next = iterator.next();
		assertEquals("MINIMAL", next.getDescription());
		assertTrue(next.isInState(TaskState.CURRENT));
		assertEquals(rootTask, next.getParent());
		assertFalse(next.hasChildren());
		//
		next = iterator.next();
		assertEquals("theName", next.getDescription());
		assertTrue(next.isInState(TaskState.CURRENT));
		assertEquals(rootTask, next.getParent());
		assertFalse(next.hasChildren());
		//
		next = iterator.next();
		assertEquals("NUMBERDIR-42", next.getDescription());
		assertTrue(next.isInState(TaskState.CURRENT));
		assertEquals(rootTask, next.getParent());
		assertFalse(next.hasChildren());
		//
		assertFalse(iterator.hasNext());
	}

	/**
	 * handle a file (do not mind contents).
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void handleAFile() throws IOException {
		cut = setUpWithBaseDir(new File(TEST_BASE_DIR, "fileBaseDir"));
		expect(spmFormatFactoryMock.construct("CURRENT")).andReturn(spmFormatMock);
		spmFormatMock.parseLines(eq(rootTask), anyObject(List.class));
		expect(spmFormatFactoryMock.construct("STATUS")).andReturn(spmFormatMock);
		spmFormatMock.parseLines(eq(rootTask), anyObject(List.class));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		assertFalse(rootTask.hasChildren());
		// TODO some more to assert?
	}

	/**
	 * handle sub and super directories. Also check path component.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@Test
	public void handleDownAndUp() throws IOException {
		cut = setUpWithBaseDir(new File(TEST_BASE_DIR, "treeBaseDir"));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		Iterator<Task> subTasks = rootTask.iterator();
		DirectoryTask sub1 = (DirectoryTask) subTasks.next();
		DirectoryTask sub1sub1 = (DirectoryTask) sub1.iterator().next();
		DirectoryTask sub2 = (DirectoryTask) subTasks.next();
		assertFalse(subTasks.hasNext());
		assertFalse(sub1sub1.hasChildren());
		assertFalse(sub2.hasChildren());
		assertEquals("./SUB-1", sub1.getPath());
		assertEquals("./SUB-1/SUB-1_SUB-1", sub1sub1.getPath());
		assertEquals("./SUB-2", sub2.getPath());
	}

	/**
	 * ignore completed directories.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@Test
	public void ignoreCompleted() throws IOException {
		cut = setUpWithBaseDir(new File(TEST_BASE_DIR, "completedBaseDir"));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		Iterator<Task> subTasks = rootTask.iterator();
		Task sub1 = subTasks.next();
		Task sub2 = subTasks.next();
		assertFalse(subTasks.hasNext());
		assertFalse(sub1.hasChildren());
		assertFalse(sub2.hasChildren());
	}

	/**
	 * use completed directories.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@SuppressWarnings("unchecked")
	@Test
	public void useCompleted() throws IOException {
		cut = setUpWithBaseDirNotFilter(new File(TEST_BASE_DIR, "completedBaseDir"));
		expect(spmFormatFactoryMock.construct("COMPLETED")).andReturn(spmFormatMock);
		spmFormatMock.parseLines(anyObject(Task.class), anyObject(List.class));
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		Iterator<Task> subTasks = rootTask.iterator();
		Task sub1 = subTasks.next();
		Task sub2 = subTasks.next();
		Task sub3 = subTasks.next();
		assertFalse(subTasks.hasNext());
		assertFalse(sub1.hasChildren());
		assertFalse(sub2.hasChildren());
		assertFalse(sub3.hasChildren());
	}

	/**
	 * Empty dir creates emtpty ROOT.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@Test
	public void handleOtherConstructor() throws IOException {
		cut = new TaskFileWalker(rootTask, new File(TEST_BASE_DIR, "emptyBaseDir"), ioFileFilter);
		ems.replayAll();
		cut.crawl();
		ems.verifyAll();
		assertFalse(rootTask.hasChildren());
	}
}
