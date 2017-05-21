package de.japrost.staproma.task;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

import de.japrost.staproma.TaskState;

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
		cut.setState(TaskState.CURRENT);
		assertTrue(cut.isInState(null));
	}

	/**
	 * Tasks must return {@code true} on {@code null} input.
	 */
	@Test
	public void everyStateIfNoChildren() {
		assertTrue(cut.isInState(TaskState.CURRENT));
		assertTrue(cut.isInState(TaskState.SCHEDULE));
		assertTrue(cut.isInState(TaskState.DONE));
		assertTrue(cut.isInState(TaskState.FUTURE));
	}

	/**
	 * Directory task returns own state after asking children.
	 */
	@Test
	public void ownStateAfterChildState() {
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(false);
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(false);
		ems.replayAll();
		cut.setState(TaskState.CURRENT);
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertTrue(cut.isInState(TaskState.CURRENT));
		ems.verifyAll();
	}

	/**
	 * Directory task uses child state.
	 */
	@Test
	public void childStateBeforeOwnState() {
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(false);
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(true);
		ems.replayAll();
		cut.setState(TaskState.SCHEDULE);
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertTrue(cut.isInState(TaskState.CURRENT));
		ems.verifyAll();
	}

	/**
	 * Directory task returns own state after asking children.
	 */
	@Test
	public void falseAsFallbackState() {
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(false);
		expect(taskMock.isInState(TaskState.CURRENT)).andReturn(false);
		ems.replayAll();
		cut.setState(TaskState.SOMEDAY);
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		assertFalse(cut.isInState(TaskState.CURRENT));
		ems.verifyAll();
	}
}
