package de.japrost.staproma.spm;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import de.japrost.staproma.TaskState;

/**
 * Test the {@link DefaultSpmFactory}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestDefaultSpmFactory {
	DefaultSpmFactory cut;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new DefaultSpmFactory();
	}

	/**
	 * Do not fail on null input.
	 */
	@Test
	public void constructIsNullSave() {
		SpmFormat actual = cut.construct(null);
		Assert.assertNotNull(actual);
	}

	/**
	 * Return a SimpleSpmFormat.
	 */
	@Test
	public void constructSimple() {
		SpmFormat actual = cut.construct(TaskState.CURRENT.name());
		Assert.assertTrue(actual instanceof SimpleSpmFormat);
	}

	/**
	 * Return a SimpleSpmFormat.
	 */
	@Test
	public void constructGTD() {
		SpmFormat actual = cut.construct("GTD");
		Assert.assertTrue(actual instanceof GtdSpmFormat);
	}
}
