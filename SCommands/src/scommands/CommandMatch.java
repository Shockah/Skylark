package scommands;

public final class CommandMatch {
	public static final double
		LOW_PRIORITY = 0d,
		MEDIUM_PRIORITY = 500d,
		HIGH_PRIORITY = 1000d;
	
	public final Command command;
	public final boolean perfectMatch;
	public final double priority;
	
	public CommandMatch(Command command, boolean perfectMatch) {
		this(command, perfectMatch, LOW_PRIORITY);
	}
	public CommandMatch(Command command, boolean perfectMatch, double priority) {
		this.command = command;
		this.perfectMatch = perfectMatch;
		this.priority = priority;
	}
}