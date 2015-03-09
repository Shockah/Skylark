package scommands;

import java.util.ArrayDeque;
import java.util.Deque;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public final class CommandStack {
	public static final int
		MAX_STACK_SIZE = 25;
	
	public final GenericUserMessageEvent event;
	public final Deque<CommandStackEntry> stack = new ArrayDeque<>(MAX_STACK_SIZE);
	
	public CommandStack(GenericUserMessageEvent e) {
		event = e;
	}
	
	public Object call(Command<?, ?> command, Object input) {
		if (command.clsInput.isInstance(input)) {
			if (stack.size() < MAX_STACK_SIZE) {
				stack.push(new CommandStackEntry(command, input));
				Object result = command.callGeneric(event, input, this);
				stack.pop();
				return result;
			} else {
				throw new RuntimeException("Stack overflow.");
			}
		} else {
			throw new RuntimeException("Invalid input type passed to the command.");
		}
	}
}