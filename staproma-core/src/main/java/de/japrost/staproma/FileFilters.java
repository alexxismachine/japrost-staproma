package de.japrost.staproma;

import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.apache.commons.io.filefilter.RegexFileFilter;

/**
 * Definitions of file filters.
 */
class FileFilters {

	private static final String
	// tag::filters[]
	SPM = "^([A-Z]+)-(\\d+)_(.*)\\.spm$"; // e.g. "PROJECT-4711_Something To Do.spm"
	// end::filters[]
	private static final String
	// tag::filters[]
	GTD = "^\\d*_?gtd\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	DONE = "^\\d*_?done\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	SOMEDAY = "^\\d*_?someday\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	FUTURE = "^\\d*_?future\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	SCHEDULE = "^\\d*_?schedule\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	WAITING = "^\\d*_?waiting\\..*$";
	// end::filters[]
	private static final String
	// tag::filters[]
	CURRENT = "^\\d*_?current\\..*$";
	// end::filters[]

	/** Case sensitive file filter for CURRENT */
	final IOFileFilter currentFilter = sensitiveRegexFileFilter(CURRENT);
	/** Case sensitive file filter for WAITING */
	final IOFileFilter waitingFilter = sensitiveRegexFileFilter(WAITING);
	/** Case sensitive file filter for SCHEDULE */
	final IOFileFilter scheduleFilter = sensitiveRegexFileFilter(SCHEDULE);
	/** Case sensitive file filter for FUTURE */
	final IOFileFilter futureFilter = sensitiveRegexFileFilter(FUTURE);
	/** Case sensitive file filter for SOMEDAY */
	final IOFileFilter somedayFilter = sensitiveRegexFileFilter(SOMEDAY);
	/** Case sensitive file filter for DONE */
	final IOFileFilter doneFilter = sensitiveRegexFileFilter(DONE);
	/** Case sensitive file filter for GTD */
	final IOFileFilter gtdFilter = sensitiveRegexFileFilter(GTD);
	/** Case sensitive file filter for SPM */
	final IOFileFilter gtd2Filter = sensitiveRegexFileFilter(SPM);

	/**
	 * Create a case sensitive filter using the given regex and matching only on files.
	 *
	 * @param regex the regex to match.
	 * @return the file filter.
	 */
	private IOFileFilter sensitiveRegexFileFilter(final String regex) {
		return FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter(regex, IOCase.SENSITIVE));
	}
}
