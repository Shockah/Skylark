package io.shockah.skylark.commands;

public class ChainCommand<T, R> extends Command<T, R> {
	private final Command<?, ?>[] commands;
	
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = commands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CommandValue<R> call(CommandCall call, T input) {
		CommandValue<?> value = new CommandValue<>(input);
		for (Command<?, ?> genericCommand : commands) {
			Command<Object, ?> objectCommand = (Command<Object, ?>)genericCommand;
			value = objectCommand.call(call, value.result);
		}
		return (CommandValue<R>)value;
	}
}