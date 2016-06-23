package io.shockah.skylark;

public class DelegatePassthroughException extends RuntimeException {
	private static final long serialVersionUID = -5052809567648125741L;
	
	public DelegatePassthroughException(Throwable cause) {
		super(cause);
	}
}