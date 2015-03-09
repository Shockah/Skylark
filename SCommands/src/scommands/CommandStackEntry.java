package scommands;

public final class CommandStackEntry {
	public final Command<?, ?> command;
	public final Object input;
	
	public CommandStackEntry(Command<?, ?> command, Object input) {
		this.command = command;
		this.input = input;
	}
}