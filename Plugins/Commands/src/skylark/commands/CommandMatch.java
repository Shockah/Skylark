package skylark.commands;

public class CommandMatch {
	public final String command;
	public final String args;
	
	public CommandMatch(String command, String args) {
		this.command = command;
		this.args = args;
	}
}