package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public abstract class Command<T, R> {
	public Integer getLineLimit(CommandCall call, T input) {
		//return null;
		
		int maxLines = 3;
		if (call.inputMedium == CommandCall.Medium.Channel && call.event.getChannel().isOp(call.event.getUser()))
			maxLines = 5;
		return maxLines;
	}
	
	public T prepareChainedCallInput(GenericUserMessageEvent e, CommandResult<T> previousResult) {
		return previousResult.value;
	}
	
	public T convertToInput(GenericUserMessageEvent e, Object input) throws CommandParseException {
		if (input == null)
			return parseInput(e, null);
		return parseInput(e, input.toString());
	}
	
	public abstract T parseInput(GenericUserMessageEvent e, String input) throws CommandParseException;
	
	public abstract CommandResult<R> call(CommandCall call, T input);
}