package de.japrost.staproma.renderer.freemarker;

import java.io.File;
import java.io.IOException;

import freemarker.template.Configuration;
import freemarker.template.TemplateExceptionHandler;

/**
 * Configuration for freemarker.
 */
public class FreemarkerConfig {

	/**
	 * Create a freemarker configuation using the templates from the given dir.
	 *
	 * @param templateDir the base directory for tempaltes
	 * @return the configuraion.
	 * @throws IOException on initalization failures.
	 */
	public static Configuration createConfiguration(final File templateDir) throws IOException {
		Configuration cfg = new Configuration(Configuration.VERSION_2_3_30);
		cfg.setDirectoryForTemplateLoading(templateDir);
		cfg.setDefaultEncoding("UTF-8");
		cfg.setTemplateExceptionHandler(TemplateExceptionHandler.RETHROW_HANDLER);
		cfg.setLogTemplateExceptions(false);
		cfg.setWrapUncheckedExceptions(true);
		cfg.setFallbackOnNullLoopVariable(false);
		return cfg;
	}
}
