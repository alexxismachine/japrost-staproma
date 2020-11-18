package de.japrost.staproma;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;

public class StyleCopy {

	/** The base dir to write to. */
	private final File outDir;

	StyleCopy(final File outDir) {
		this.outDir = outDir;
	}

	void copyStyle() throws IOException {
		final File srcCssFile = new File("src/main/resources/style.css");
		if (srcCssFile.canRead()) {
			final File destCssFile = new File(outDir, "style.css");
			FileUtils.copyFile(srcCssFile, destCssFile);
		}
	}

}
