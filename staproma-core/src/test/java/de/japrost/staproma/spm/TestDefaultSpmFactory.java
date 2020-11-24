package de.japrost.staproma.spm;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import de.japrost.staproma.TaskState;

/**
 * Test the {@link DefaultSpmFactory}.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestDefaultSpmFactory {

	private DefaultSpmFactory cut;

	/**
	 * Set up each test.
	 */
	@BeforeEach
	void setUp() {
		cut = new DefaultSpmFactory();
	}

	/**
	 * Do not fail on null input.
	 */
	@Test
	void constructIsNullSave() {
		SpmFormat actual = cut.construct(null);
		assertNotNull(actual);
	}

	/**
	 * Return a SimpleSpmFormat.
	 */
	@Test
	void constructSimple() {
		SpmFormat actual = cut.construct(TaskState.CURRENT.name());
		assertTrue(actual instanceof SimpleSpmFormat);
	}

	/**
	 * Return a SimpleSpmFormat.
	 */
	@Test
	void constructGTD() {
		SpmFormat actual = cut.construct("GTD");
		assertTrue(actual instanceof GtdSpmFormat);
	}
}
