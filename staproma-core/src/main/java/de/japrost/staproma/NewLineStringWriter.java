package de.japrost.staproma;

import java.io.StringWriter;

public class NewLineStringWriter extends StringWriter {

	@Override
	public StringWriter append(final CharSequence csq) {
		write(String.valueOf(csq));
		write("\n");
		return this;
	}

}
