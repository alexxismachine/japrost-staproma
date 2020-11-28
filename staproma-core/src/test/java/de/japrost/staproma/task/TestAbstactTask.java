/**
 *
 */
package de.japrost.staproma.task;

import static org.easymock.EasyMock.expect;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.japrost.staproma.TaskState;

/**
 * Test the {@link AbstactTask}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestAbstactTask {

	private AbstactTask cut;
	private EasyMockSupport ems;
	private Task parentTask;
	private Task taskMock;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		ems = new EasyMockSupport();
		parentTask = new AnonymousTask(null);
		cut = new AbstactTask(parentTask, "The root task") {

			@Override
			public boolean isContainer() {
				return false;
			}
		};
		taskMock = ems.createMock(Task.class);
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	void getConstructorAssignments() {
		assertEquals("The root task", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}

	/**
	 * Test setting the state.
	 */
	@Test
	void setState() {
		assertFalse(cut.isInState(TaskState.CURRENT));
		cut.setState(TaskState.CURRENT);
		assertTrue(cut.isInState(TaskState.CURRENT));
	}

	/**
	 * Abstract task has no own state initially.
	 */
	@Test
	void isInFalseStateByDefault() {
		assertFalse(cut.isInState(TaskState.CURRENT));
	}

	/**
	 * Tasks must return {@code true} on {@code null} input.
	 */
	@Test
	void tasksAreInNullState() {
		cut.setState(TaskState.CURRENT);
		assertTrue(cut.isInState(null));
	}

	/**
	 * Abstract task returns own state before asking children.
	 */
	@Test
	void ownStateBeforeChildState() {
		// taskMock is not called.
		ems.replayAll();
		cut.setState(TaskState.CURRENT);
		cut.addChild(taskMock);
		assertTrue(cut.isInState(TaskState.CURRENT));
		ems.verifyAll();
	}

	/**
	 * Abstract task asks children if own state does not match.
	 */
	@Test
	void childStateIfNotOwnState() {
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
	 * Abstract task implements iterable on children.
	 */
	@Test
	void implementsIterableOnSubTasks() {
		ems.replayAll();
		cut.setState(TaskState.CURRENT);
		cut.addChild(taskMock);
		cut.addChild(taskMock);
		int count = 0;
		for (@SuppressWarnings("unused")
		Task subTask : cut) {
			count++;
		}
		ems.verifyAll();
		assertEquals(2, count);
	}

	/**
	 * Abstract has no children initially.
	 */
	@Test
	void noChildInitially() {
		assertFalse(cut.hasChildren());
	}

	/**
	 * Abstract tells to have children.
	 */
	@Test
	void iHaveAChild() {
		ems.replayAll();
		cut.addChild(taskMock);
		ems.verifyAll();
		assertTrue(cut.hasChildren());
	}

	/**
	 * No content initially.
	 */
	@Test
	void noContentInitially() {
		assertEquals(0, cut.getContent().size());
	}

	/**
	 * content just stored.
	 */
	@Test
	void storeContent() {
		cut.addContent("first line");
		cut.addContent(null);
		cut.addContent("");
		cut.addContent("last line");
		List<String> content = cut.getContent();
		assertEquals(4, content.size());
		assertEquals("first line", content.get(0));
		assertNull(content.get(1));
		assertEquals("", content.get(2));
		assertEquals("last line", content.get(3));
	}
}
