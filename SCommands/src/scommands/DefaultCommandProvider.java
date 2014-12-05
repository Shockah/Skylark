package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.Pair;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public class DefaultCommandProvider extends CommandProvider {
	protected List<Command> list = Collections.synchronizedList(new LinkedList<Command>());
	
	public DefaultCommandProvider(Plugin plugin) {
		super(plugin);
	}
	
	public void add(Command... cmds) {
		synchronized (list) {for (Command cmd : cmds) {
			if (!list.contains(cmd)) {
				list.add(cmd);
			}
		}}
	}
	public void remove(Command... cmds) {
		synchronized (list) {for (Command cmd : cmds) {
			list.remove(cmd);
		}}
	}
	
	public void provide(List<Pair<ICommand, EPriority>> candidates, GenericUserMessageEvent e, String trigger, String args, CommandResult result) {
		synchronized (list) {
			for (Command cmd : list) {
				if (cmd.main.equals(trigger)) {
					candidates.add(new Pair<ICommand, EPriority>(cmd, EPriority.High));
					return;
				}
			}
			for (Command cmd : list) {
				for (String alt : cmd.alt) {
					if (alt.equals(trigger)) {
						candidates.add(new Pair<ICommand, EPriority>(cmd, EPriority.High));
						return;
					}
				}
			}
			
			Command closest = null;
			int diff = -1;
			for (Command cmd : list) {
				if (cmd.main.startsWith(trigger)) {
					int d = cmd.main.length() - trigger.length();
					if (closest == null || d < diff) {
						closest = cmd;
						diff = d;
					}
				}
				for (String alt : cmd.alt) {
					if (alt.startsWith(trigger)) {
						int d = alt.length() - trigger.length();
						if (closest == null || d < diff) {
							closest = cmd;
							diff = d;
						}
					}
				}
			}
			
			if (closest != null) {
				candidates.add(new Pair<ICommand, EPriority>(closest, EPriority.Low));
			}
		}
	}
}