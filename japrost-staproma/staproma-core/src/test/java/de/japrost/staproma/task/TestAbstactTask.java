/**
 * 
 */
package de.japrost.staproma.task;

import static org.easymock.EasyMock.expect;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.easymock.EasyMockSupport;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link AbstactTask}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestAbstactTask {
	private AbstactTask cut;
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
		cut = new AbstactTask(parentTask, "The root task") {
		};
		taskMock = ems.createMock(Task.class);
	}

	/**
	 * Test if the constructor values get assigned.
	 */
	@Test
	public void getConstructorAssignments() {
		assertEquals("The root task", cut.getDescription());
		assertEquals(parentTask, cut.getParent());
	}

	/**
	 * Test setting the state.
	 */
	@Test
	public void setState() {
		assertFalse(cut.isInState("ULI"));
		cut.setState("ULI");
		assertTrue(cut.isInState("ULI"));
	}

	/**
	 * Abstract task has no own state initially.
	 */
	@Test
	public void isInFalseStateByDefault() {
		assertFalse(cut.isInState(""));
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
	 * Abstract task returns own state before asking children.
	 */
	@Test
	public void ownStateBeforeChildState() {
		// taskMock is not called.
		ems.replayAll();
		cut.setState("ULI");
		cut.addChild(taskMock);
		assertTrue(cut.isInState("ULI"));
		ems.verifyAll();
	}

	/**
	 * Abstract task asks children if own state does not match.
	 */
	@Test
	public void childStateIfNotOwnState() {
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
	 * Abstract task implements iterable on children.
	 */
	@Test
	public void implementsIterableOnSubTasks() {
		ems.replayAll();
		cut.setState("PETER");
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
	public void noChildInitially() {
		assertFalse(cut.hasChildren());
	}

	/**
	 * Abstract tells to have children.
	 */
	@Test
	public void iHaveAChild() {
		ems.replayAll();
		cut.addChild(taskMock);
		ems.verifyAll();
		assertTrue(cut.hasChildren());
	}

	/**
	 * No content initially.
	 */
	@Test
	public void noContentInitially() {
		assertEquals(0, cut.getContent().size());
	}

	/**
	 * content just stored.
	 */
	@Test
	public void storeContent() {
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
