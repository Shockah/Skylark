package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public class ChainCommand<T, R> extends Command<T, R> {
	private final Command<?, ?>[] commands;
	
	public ChainCommand(Command<?, ?>[] commands) {
		this.commands = commands;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T prepareChainedCallInput(GenericUserMessageEvent e, CommandResult<T> previousResult) {
		Command<Object, Object> objectCommand = (Command<Object, Object>)commands[0];
		return (T)objectCommand.prepareChainedCallInput(e, (CommandResult<Object>)previousResult);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public T convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		return (T)commands[0].convertToInput(e, input);
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
		CommandResult<?> previousResult = null;
		for (Command<?, ?> genericCommand : commands) {
			Command<Object, Object> objectCommand = (Command<Object, Object>)genericCommand;
			Object inputToCall = value;
			if (previousResult != null)
				inputToCall = objectCommand.prepareChainedCallInput(call.event, (CommandResult<Object>)previousResult);
			try {
				inputToCall = objectCommand.convertToInput(call.event, inputToCall);
			} catch (Exception e) {
				return CommandResult.error(e.getMessage());
			}
			previousResult = objectCommand.call(call, inputToCall);
			if (previousResult.error != null)
				return CommandResult.error(previousResult.error);
			value = previousResult.value;
		}
		return CommandResult.of((R)value);
	}
}