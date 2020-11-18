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
	SPM = "^([A-Z]+)-(\\d+)_(.*)\\.spm$";
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

	final IOFileFilter currentFilter = sensitiveRegexFileFilter(CURRENT);
	final IOFileFilter waitingFilter = sensitiveRegexFileFilter(WAITING);
	final IOFileFilter scheduleFilter = sensitiveRegexFileFilter(SCHEDULE);
	final IOFileFilter futureFilter = sensitiveRegexFileFilter(FUTURE);
	final IOFileFilter somedayFilter = sensitiveRegexFileFilter(SOMEDAY);
	final IOFileFilter doneFilter = sensitiveRegexFileFilter(DONE);
	final IOFileFilter gtdFilter = sensitiveRegexFileFilter(GTD);
	final IOFileFilter gtd2Filter = sensitiveRegexFileFilter(SPM);

	private IOFileFilter sensitiveRegexFileFilter(final String regex) {
		return FileFilterUtils.and(FileFilterUtils.fileFileFilter(),
				new RegexFileFilter(regex, IOCase.SENSITIVE));
	}
}
