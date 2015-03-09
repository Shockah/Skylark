package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class DefaultCommandProvider extends CommandProvider {
	public final List<Command<?, ?>> commands = Collections.synchronizedList(new LinkedList<Command<?, ?>>());
	
	public DefaultCommandProvider(Plugin plugin) {
		super(plugin, MEDIUM_PRIORITY);
	}
	
	public void add(Command<?, ?>... commands) {
		synchronized (this.commands) {for (Command<?, ?> command : commands)
			this.commands.add(command);
		}
	}
	public void remove(Command<?, ?>... commands) {
		synchronized (this.commands) {for (Command<?, ?> command : commands)
			this.commands.remove(command);
		}
	}
	public void removeAll(shocky3.Plugin plugin) {
		List<Command<?, ?>> list = new LinkedList<>();
		synchronized (commands) {
			for (Command<?, ?> command : commands)
				if (command.plugin == plugin)
					list.add(command);
			commands.removeAll(list);
		}
	}
	
	public CommandMatch provide(GenericUserMessageEvent e, String name, String input) {
		name = name.toLowerCase();
		synchronized (commands) {
			for (Command<?, ?> command : commands) {
				if (command.main.toLowerCase().equals(name))
					return new CommandMatch(command, true, priority);
			}
			for (Command<?, ?> command : commands)
				for (String alt : command.alts)
					if (alt.toLowerCase().equals(name))
						return new CommandMatch(command, true, priority);
			
			Command<?, ?> closest = null;
			int diff = -1;
			for (Command<?, ?> command : commands) {
				if (command.main.toLowerCase().startsWith(name)) {
					int thisdiff = command.main.length() - name.length();
					if (closest == null || thisdiff < diff) {
						closest = command;
						diff = thisdiff;
					}
				}
			}
			for (Command<?, ?> command : commands) {
				for (String alt : command.alts) {
					if (alt.toLowerCase().startsWith(name)) {
						int thisdiff = alt.length() - name.length();
						if (closest == null || thisdiff < diff) {
							closest = command;
							diff = thisdiff;
						}
					}
				}
			}
			if (closest != null)
				return new CommandMatch(closest, false, priority);
		}
		
		return null;
	}
}