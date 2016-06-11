package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public abstract class CommandProvider {
	public abstract Command provide(GenericUserMessageEvent e, String commandName, String input);
}