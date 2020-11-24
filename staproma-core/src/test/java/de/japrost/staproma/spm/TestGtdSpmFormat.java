package de.japrost.staproma.spm;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.japrost.staproma.TaskState;
import de.japrost.staproma.task.AnonymousTask;
import de.japrost.staproma.task.Task;

/**
 * Test the {@link GtdSpmFormat}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestGtdSpmFormat {

	private GtdSpmFormat cut;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		cut = new GtdSpmFormat();
	}

	/**
	 * Parsing empty lines does no harm.
	 */
	@Test
	void parseEmptyLines() {
		List<String> lines = new ArrayList<>();
		Task result = cut.parseLines(lines);
		assertFalse(result.hasChildren());
	}

	/**
	 * Basic topic parse test.
	 */
	@Test
	void parseSingleTopicLine() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		Task result = cut.parseLines(lines);
		assertEquals("First Topic", result.iterator().next().getDescription());
	}

	/**
	 * Lines at same level go to same level.
	 */
	@Test
	void parse2TopicLinesOnSameLevel() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		lines.add("# Second Topic");
		Task result = cut.parseLines(lines);
		Iterator<Task> iterator = result.iterator();
		assertEquals("First Topic", iterator.next().getDescription());
		assertEquals("Second Topic", iterator.next().getDescription());
	}

	/**
	 * Lines at different level create hierarchie.
	 */
	@Test
	void parseTopicAndSubTopic() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		lines.add("## Sub-Topic");
		Task result = cut.parseLines(lines);
		Iterator<Task> iterator = result.iterator();
		Task firstTopic = iterator.next();
		assertEquals("First Topic", firstTopic.getDescription());
		assertEquals("Sub-Topic", firstTopic.iterator().next().getDescription());
	}

	/**
	 * Levels going up an down are representd in hierarachie.
	 */
	@Test
	void parseTopicLevelDownAndUp() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		lines.add("## Sub-Topic1");
		lines.add("# Second Topic");
		lines.add("## Sub-Topic2");
		Task result = cut.parseLines(lines);
		Iterator<Task> iterator = result.iterator();
		Task firstTopic = iterator.next();
		assertEquals("First Topic", firstTopic.getDescription());
		Task secondTopic = iterator.next();
		assertEquals("Second Topic", secondTopic.getDescription());
		assertEquals("Sub-Topic1", firstTopic.iterator().next().getDescription());
		assertEquals("Sub-Topic2", secondTopic.iterator().next().getDescription());
	}

	/**
	 * Missing level going down the hierarchie is filled up with an intermediate task.
	 */
	@Test
	void parseTopicLevelAdjustmentDown() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		// ## intermediate topic
		lines.add("### Sub-Sub-Topic1");
		Task result = cut.parseLines(lines);
		Iterator<Task> iterator = result.iterator();
		Task firstTopic = iterator.next();
		Task intermediateTopic = firstTopic.iterator().next();
		Task subSubTopic = intermediateTopic.iterator().next();
		assertEquals("First Topic", firstTopic.getDescription());
		assertEquals(AnonymousTask.ANONYMOUS_TASK_DESCRIPTION, intermediateTopic.getDescription());
		assertEquals("Sub-Sub-Topic1", subSubTopic.getDescription());
	}

	/**
	 * Missiong level going up the hierarchie leads to addtion to higher task.
	 */
	@Test
	void parseTopicLevelAdjustmentUp() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		lines.add("## Sub-Topic1");
		lines.add("### Sub-Sub-Topic1");
		// no intermediate topic required
		lines.add("# Second Topic");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstTopic = rootIterator.next();
		Iterator<Task> firstIterator = firstTopic.iterator();
		Task subTopic = firstIterator.next();
		Task subSubTopic = subTopic.iterator().next();
		Task secondTopic = rootIterator.next();
		assertEquals("First Topic", firstTopic.getDescription());
		assertEquals("Sub-Topic1", subTopic.getDescription());
		assertEquals("Sub-Sub-Topic1", subSubTopic.getDescription());
		assertFalse(firstIterator.hasNext());
		assertEquals("Second Topic", secondTopic.getDescription());
	}

	/**
	 * Base parsing a invalid action.
	 */
	@Test
	void parseInvalidAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (\f) invalid");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("(\f) invalid", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.CURRENT));
	}

	/**
	 * Base parsing a current action.
	 */
	@Test
	void parseCurrentAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (!) current");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("current", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.CURRENT));
		assertEquals(1, firstAction.getPriority());

	}

	/**
	 * Base parsing a schedule action.
	 */
	@Test
	void parseScheduleAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (@) schedule");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("schedule", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.SCHEDULE));
	}

	/**
	 * Base parsing a done action.
	 */
	@Test
	void parseDoneAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (/) done");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("done", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.DONE));
	}

	/**
	 * Base parsing a someday action.
	 */
	@Test
	void parseSomedayAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (?) someday");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("someday", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.SOMEDAY));
	}

	/**
	 * Base parsing a future action.
	 */
	@Test
	void parseFutureAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (#) future");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("future", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.FUTURE));
	}

	/**
	 * Base parsing a waiting action.
	 */
	@Test
	void parseWaitingAction() {
		List<String> lines = new ArrayList<>();
		lines.add("* (:) waiting");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("waiting", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.WAITING));
	}

	/**
	 * Base parsing a priority action.
	 */
	@Test
	void parsePriorityAction4() {
		List<String> lines = new ArrayList<>();
		lines.add("* (4) prio 4");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("prio 4", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.CURRENT));
		assertEquals(4, firstAction.getPriority());
	}

	/**
	 * Action lines without symbol are parsed as current action.
	 */
	@Test
	void parseMissingActionIsCurrent() {
		List<String> lines = new ArrayList<>();
		lines.add("* missing (in) action");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("missing (in) action", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.CURRENT));
	}

	/**
	 * Action lines with unknown symbol are parsed as current action.
	 */
	@Test
	void parseUnknownActionIsCurrent() {
		List<String> lines = new ArrayList<>();
		lines.add("* (+) unknown action");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstAction = rootIterator.next();
		assertEquals("(+) unknown action", firstAction.getDescription());
		assertTrue(firstAction.isInState(TaskState.CURRENT));
	}

	/**
	 * Action lines before first level are added to root task.<br>
	 * This IS subject to change.
	 */
	@Test
	void parseContentBeforeFirstTask() {
		List<String> lines = new ArrayList<>();
		lines.add("some content");
		Task result = cut.parseLines(lines);
		// IST:
		assertEquals("some content", result.getContent().iterator().next());
		// FIXME: mit intermediate task!
		//		Iterator<Task> rootIterator = result.iterator();
		//		Task firstAction = rootIterator.next();
		//		Iterator<Task> firstIterator = firstAction.iterator();
		//		assertEquals("(+) unknown action", firstAction.getDescription());
		//		assertTrue(firstAction.isInState("CURRENT"));
	}

	/**
	 * Content lines are parsed to current task.
	 */
	@Test
	void parseContentToTask() {
		List<String> lines = new ArrayList<>();
		lines.add("# First Topic");
		lines.add("some content");
		Task result = cut.parseLines(lines);
		Iterator<Task> rootIterator = result.iterator();
		Task firstTopic = rootIterator.next();
		assertEquals("First Topic", firstTopic.getDescription());
		Iterator<String> contentIterator = firstTopic.getContent().iterator();
		assertEquals("some content", contentIterator.next());
		assertFalse(contentIterator.hasNext());
	}
}
