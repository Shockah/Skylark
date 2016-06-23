package io.shockah.skylark.commands;

public class CommandParseException extends Exception {
	private static final long serialVersionUID = -1260914648331453444L;
	
	public CommandParseException() {
		super();
	}
	
	public CommandParseException(String message) {
		super(message);
	}
	
	public CommandParseException(Throwable cause) {
		super(cause);
	}
}