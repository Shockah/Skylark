package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public class ChainCommand<T, R> extends Command<T, R> {
	private final Command<?, ?>[] commands;
	
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = commands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return (T)commands[0].parseInput(e, input);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CommandValue<R> call(CommandCall call, T input) {
		CommandValue<?> value = new CommandValue<>(input);
		for (Command<?, ?> genericCommand : commands) {
			Command<Object, ?> objectCommand = (Command<Object, ?>)genericCommand;
			value = objectCommand.call(call, value.result);
			if (value.error != null)
				break;
		}
		return (CommandValue<R>)value;
	}
}