package de.japrost.staproma.task;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link LeafTask}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestLeafTask {
	private LeafTask cut;
	private Task parentTask;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new LeafTask(parentTask, "The leaf task.");
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	public void getConstructorAssignments() {
		assertEquals("The leaf task.", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}

	/**
	 * a leaf taks must no have children.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void noChildsAssignable() {
		cut.addChild(new AnonymousTask(cut));
	}
}
