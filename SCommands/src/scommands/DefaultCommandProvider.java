package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.Pair;
import shocky3.Shocky;

public class DefaultCommandProvider extends CommandProvider {
	protected List<Command> list = Collections.synchronizedList(new LinkedList<Command>());
	
	public DefaultCommandProvider(Plugin plugin) {
		super(plugin);
	}
	
	public void add(Command... cmds) {
		for (Command cmd : cmds) {
			if (!list.contains(cmd)) {
				list.add(cmd);
			}
		}
	}
	public void remove(Command... cmds) {
		for (Command cmd : cmds) {
			list.remove(cmd);
		}
	}
	
	public void provide(List<Pair<ICommand, EPriority>> candidates, Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args) {
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