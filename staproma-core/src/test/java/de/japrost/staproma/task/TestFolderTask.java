package de.japrost.staproma.task;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link FolderTask}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestFolderTask {
	private FolderTask cut;
	private Task parentTask;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new FolderTask(parentTask, "The folder task.");
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	public void getConstructorAssignments() {
		assertEquals("The folder task.", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}
}
