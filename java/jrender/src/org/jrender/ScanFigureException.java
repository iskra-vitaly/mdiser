package org.jrender;

public class ScanFigureException extends Exception {
	private static final long serialVersionUID = 1L;

	public ScanFigureException() {
	}

	public ScanFigureException(String message) {
		super(message);
	}

	public ScanFigureException(Throwable cause) {
		super(cause);
	}

	public ScanFigureException(String message, Throwable cause) {
		super(message, cause);
	}
}
