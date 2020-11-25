package de.japrost.staproma;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Test;

/**
 * Test the main class.
 *
 * @author alexxismachine (Ulrich David)
 */
class TestRenderStaProMa2 {

	/**
	 * test.
	 *
	 * @throws IOException on io failure.
	 */
	@Test
	void noReallyATest() throws IOException {
		File outDir = new File("target/", "renderDir");
		outDir.mkdirs();
		RenderStaProMa2
				.main(new String[] { new File("src/test/resources", "renderDir").getAbsolutePath(), outDir.getAbsolutePath(),
						new File("src/test/resources", "templates").getAbsolutePath() });
		//FIXME assert file contents?
	}

	/**
	 * test.
	 *
	 * @throws IOException on io failure.
	 */
	@Test
	void renderMixedContent() throws IOException {
		File outDir = new File("target/", "mixContent");
		outDir.mkdirs();
		RenderStaProMa2.main(new String[] { new File("src/test/resources", "mixFlatAndDirectoryTasks").getAbsolutePath(),
				outDir.getAbsolutePath(), new File("src/test/resources", "templates").getAbsolutePath() });
		//FIXME assert file contents?
	}
}
