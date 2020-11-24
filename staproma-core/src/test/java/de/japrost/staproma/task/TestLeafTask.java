package de.japrost.staproma.task;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link LeafTask}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestLeafTask {

	private LeafTask cut;
	private Task parentTask;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		cut = new LeafTask(parentTask, "The leaf task.");
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	void getConstructorAssignments() {
		assertEquals("The leaf task.", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}

	/**
	 * a leaf taks must no have children.
	 */
	@Test
	void noChildsAssignable() {
		assertThrows(
				IllegalArgumentException.class,
				() -> cut.addChild(new AnonymousTask(cut)),
				"Expected addChild() to throw, but it didn't");
	}
}
