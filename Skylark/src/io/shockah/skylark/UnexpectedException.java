package io.shockah.skylark;

public class UnexpectedException extends RuntimeException {
	private static final long serialVersionUID = 5086451181161428825L;
	
	public UnexpectedException(String message) {
		super(message);
	}
	
	public UnexpectedException(Throwable cause) {
		super(cause);
	}
	
	public UnexpectedException(String message, Throwable cause) {
		super(message, cause);
	}
}