package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public class ToStringCommand extends NamedCommand<Object, String> {
	public ToStringCommand() {
		super("tostring");
	}
	
	public String prepareChainedCallInput(GenericUserMessageEvent e, CommandResult<Object> previousResult) {
		return previousResult.toString();
	}

	@Override
	public Object parseInput(GenericUserMessageEvent e, String input) throws CommandParseException {
		return input;
	}

	@Override
	public CommandResult<String> call(CommandCall call, Object input) {
		return CommandResult.of(input.toString());
	}
}