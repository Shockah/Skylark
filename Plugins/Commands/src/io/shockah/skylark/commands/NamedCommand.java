package io.shockah.skylark.commands;

public abstract class NamedCommand<T, R> extends Command<T, R> {
	protected final String[] names;
	
	public NamedCommand(String[] names) {
		this.names = names;
	}
}