package io.shockah.skylark.commands;

public final class CommandResult<T> {
	public final T value;
	public final String ircOutput;
	public final String error;
	
	private CommandResult(T value, String ircOutput, String error) {
		this.value = value;
		this.ircOutput = ircOutput;
		this.error = error;
	}
	
	public static <T> CommandResult<T> of(T value) {
		return new CommandResult<>(value, value == null ? null : value.toString(), null);
	}
	
	public static <T> CommandResult<T> of(T value, String ircOutput) {
		return new CommandResult<>(value, ircOutput, null);
	}
	
	public static <T> CommandResult<T> error(String error) {
		return new CommandResult<>(null, null, error);
	}
	
	@Override
	public String toString() {
		return error == null ? ircOutput : error;
	}
}