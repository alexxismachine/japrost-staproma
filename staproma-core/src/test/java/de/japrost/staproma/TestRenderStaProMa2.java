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
		File outDir = new File("target/", "renderDir");
		outDir.mkdirs();
		RenderStaProMa2.main(new String[] { new File("src/test/resources", "renderDir").getAbsolutePath(),outDir.getAbsolutePath() });
		//FIXME assert file contents?
	}
}
