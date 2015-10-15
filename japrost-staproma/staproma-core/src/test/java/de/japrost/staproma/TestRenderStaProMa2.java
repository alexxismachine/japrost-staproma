package de.japrost.staproma;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;

/**
 * Test the main class.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class TestRenderStaProMa2 {
	/**
	 * Set up each test.
	 */
	@Before
	public void setUp() {
	}

	/**
	 * test.
	 * 
	 * @throws IOException
	 *             on io failure.
	 */
	@Test
	public void noReallyATest() throws IOException {
		RenderStaProMa2.main(new String[] { new File("src/test/resources", "renderDir").getAbsolutePath() });
		//FIXME assert file contents?
	}
}
