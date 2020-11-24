package de.japrost.staproma.task;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test the {@link AnonymousTask}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestAnonymousTask {

	private AnonymousTask cut;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		cut = new AnonymousTask(null);
	}

	/**
	 * {@link AnonymousTask} has a default description.
	 */
	@Test
	void defaultDescription() {
		assertEquals("N/A", cut.getDescription());
	}
}
