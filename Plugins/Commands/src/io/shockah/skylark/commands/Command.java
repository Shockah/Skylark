package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public abstract class Command<T, R> {
	public Integer getLineLimit(CommandCall call, T input) {
		return null;
		
		/*int maxLines = 2;
		if (call.inputMedium == CommandCall.Medium.Channel && call.event.getChannel().isOp(call.event.getUser()))
			maxLines = 4;*/
	}
	
	public abstract T prepareInput(GenericUserMessageEvent e, String input);
	
	public abstract CommandValue<R> call(CommandCall call, T input);
}