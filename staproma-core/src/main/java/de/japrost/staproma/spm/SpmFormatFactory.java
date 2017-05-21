package de.japrost.staproma.spm;

/**
 * Factory for {@link SpmFormat}s.
 * 
 * @author alexxismachine (Ulrich David)
 * 
 */
public interface SpmFormatFactory {
	/**
	 * Construct an SpmFormat based on the given hint.MUST NOT return {@code null}.
	 * 
	 * @param hint
	 *            the hint, which SpmFormat should be choosen.
	 * @return the {@link SpmFormat} instance.
	 */
	SpmFormat construct(String hint);
}
