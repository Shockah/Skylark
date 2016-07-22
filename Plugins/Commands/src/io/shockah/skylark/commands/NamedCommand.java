package io.shockah.skylark.commands;

public abstract class NamedCommand<T, R> extends Command<T, R> {
	protected final String[] names;
	
	public NamedCommand(String name, String... extraNames) {
		String[] names = new String[extraNames.length + 1];
		names[0] = name;
		for (int i = 0; i < extraNames.length; i++)
			names[i + 1] = extraNames[i];
		this.names = names;
	}
}