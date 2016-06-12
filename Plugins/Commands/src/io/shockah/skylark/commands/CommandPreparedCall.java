package io.shockah.skylark.commands;

public final class CommandPreparedCall<T, R> {
	public final Command<T, R> command;
	public final T input;
	
	public CommandPreparedCall(Command<T, R> command, T input) {
		this.command = command;
		this.input = input;
	}
	
	public Integer getLineLimit(CommandCall call) {
		return command.getLineLimit(call, input);
	}
	
	public CommandValue<R> call(CommandCall call) {
		return command.call(call, input);
	}
}