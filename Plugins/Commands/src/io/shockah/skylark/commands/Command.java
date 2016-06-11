package io.shockah.skylark.commands;

import io.shockah.skylark.event.GenericUserMessageEvent;

public abstract class Command {
	public abstract void run(GenericUserMessageEvent e, String input);
}