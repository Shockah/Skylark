package scommands;

public final class CommandStackEntry {
	public final Command command;
	public final String input;
	
	public CommandStackEntry(Command command, String input) {
		this.command = command;
		this.input = input;
	}
}