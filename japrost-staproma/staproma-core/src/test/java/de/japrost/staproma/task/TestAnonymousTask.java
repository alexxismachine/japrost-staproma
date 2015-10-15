package de.japrost.staproma.task;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Test the {@link AnonymousTask}.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestAnonymousTask {
	AnonymousTask cut;

	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
		cut = new AnonymousTask(null);
	}

	/**
	 * {@link AnonymousTask} has a default description.
	 */
	@Test
	public void defaultDescription() {
		Assert.assertEquals("N/A", cut.getDescription());
	}
}
