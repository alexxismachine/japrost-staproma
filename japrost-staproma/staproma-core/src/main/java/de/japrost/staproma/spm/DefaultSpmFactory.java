package de.japrost.staproma.spm;

import de.japrost.staproma.TaskState;

/**
 * A {@link SpmFormat} that returns a {@link GtdSpmFormat} if the hint is
 * {@code GTD}, an {@link SimpleSpmFormat} with the hint as status else.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public class DefaultSpmFactory implements SpmFormatFactory {
	@Override
	public SpmFormat construct(String hint) {
		// TODO this must be much much more flexible and do not use "GTD".
		if ("GTD".equalsIgnoreCase(hint)) {
			return new GtdSpmFormat();
		}
		if (hint == null) {
			return new SimpleSpmFormat(null);
		}
		return new SimpleSpmFormat(TaskState.valueOf(hint));
	}
}
