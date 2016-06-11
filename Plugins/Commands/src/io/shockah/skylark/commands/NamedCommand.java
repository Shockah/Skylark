package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public class NamedCommand extends Command {
	protected final String[] names;
	
	public NamedCommand(String[] names) {
		this.names = names;
	}
	
	@Override
	public void run(GenericUserMessageEvent e, String input) {
	}
}