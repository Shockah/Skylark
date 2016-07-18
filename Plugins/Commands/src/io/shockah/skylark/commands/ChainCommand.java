package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public class ChainCommand<T, R> extends Command<T, R> {
	private final Command<?, ?>[] commands;
	
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = commands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseAnyInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		return (T)commands[0].parseAnyInput(e, input);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return (T)commands[0].parseInput(e, input);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public CommandResult<R> call(CommandCall call, T input) {
		Object value = input;
		for (Command<?, ?> genericCommand : commands) {
			Command<Object, Object> objectCommand = (Command<Object, Object>)genericCommand;
			CommandResult<?> result = objectCommand.call(call, value);
			if (result.error != null)
				CommandResult.error(result.error);
			value = result.value;
		}
		return CommandResult.of((R)value);
	}
}