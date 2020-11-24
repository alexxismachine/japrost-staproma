package de.japrost.staproma.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link FolderTask}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestFolderTask {

	private FolderTask cut;
	private Task parentTask;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		cut = new FolderTask(parentTask, "The folder task.");
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	void getConstructorAssignments() {
		assertEquals("The folder task.", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}
}
