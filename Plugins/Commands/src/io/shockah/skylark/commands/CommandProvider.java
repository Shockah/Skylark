package io.shockah.skylark.commands;

public abstract class CommandProvider {
	public abstract NamedCommand<?, ?> provide(String commandName);
}