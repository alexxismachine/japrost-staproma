package de.japrost.staproma.task;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link DirectoryTask}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestDirectoryTask {
	private DirectoryTask cut;
	private EasyMockSupport ems;
	private Task parentTask;
	private Task taskMock;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		ems = new EasyMockSupport();
		parentTask = new AnonymousTask(null);
		cut = new DirectoryTask(parentTask, "path/name", "The directory task");
		taskMock = ems.createMock(Task.class);
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	public void getConstructorAssignments() {
		assertEquals("The directory task", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
		assertEquals("path/name", cut.getPath());
	}

	/**
	 * Tasks must return {@code true} on {@code null} input.
	 */
	@Test
	public void tasksAreInNullState() {
		cut.setState("ULI");
		assertTrue(cut.isInState(null));
	}

	/**
	 * Tasks must return {@code true} on {@code null} input.
	 */
	@Test
	public void everyStateIfNoChildren() {
		assertTrue(cut.isInState("ULI"));
		assertTrue(cut.isInState("PIT"));
		assertTrue(cut.isInState("PON"));
		assertTrue(cut.isInState("XYZ"));
	}

	/**
	 * Directory task returns own state after asking children.
	 */
	@Test
	public void ownStateAfterChildState() {
		expect(taskMock.isInState("ULI")).andReturn(false);
		expect(taskMock.isInState("ULI")).andReturn(false);
		ems.replayAll();
		cut.setState("ULI");
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertTrue(cut.isInState("ULI"));
		ems.verifyAll();
	}

	/**
	 * Directory task uses child state.
	 */
	@Test
	public void childStateBeforeOwnState() {
		expect(taskMock.isInState("ULI")).andReturn(false);
		expect(taskMock.isInState("ULI")).andReturn(true);
		ems.replayAll();
		cut.setState("PETER");
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertTrue(cut.isInState("ULI"));
		ems.verifyAll();
	}

	/**
	 * Directory task returns own state after asking children.
	 */
	@Test
	public void falseAsFallbackState() {
		expect(taskMock.isInState("PIT")).andReturn(false);
		expect(taskMock.isInState("PIT")).andReturn(false);
		ems.replayAll();
		cut.setState("ULI");
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertFalse(cut.isInState("PIT"));
		ems.verifyAll();
	}
}
