package scommands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.SortedLinkedList;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public final class CommandPatternManager {
	public final List<CommandPattern> patterns = Collections.synchronizedList(new SortedLinkedList<CommandPattern>(new Comparator<CommandPattern>(){
		public int compare(CommandPattern o1, CommandPattern o2) {
			return Double.compare(o2.priority, o1.priority);
		}
	}));
	
	public CommandStackEntry matchCommand(GenericUserMessageEvent e) {
		synchronized (patterns) {for (CommandPattern pattern : patterns) {
			CommandStackEntry entry = pattern.matchCommand(e);
			if (entry != null)
				return entry;
		}}
		return null;
	}
	
	public void add(CommandPattern... patterns) {
		synchronized (this.patterns) {for (CommandPattern pattern : patterns)
			this.patterns.add(pattern);
		}
	}
	public void remove(CommandPattern... patterns) {
		synchronized (this.patterns) {for (CommandPattern pattern : patterns)
			this.patterns.remove(pattern);
		}
	}
	public void removeAll(shocky3.Plugin plugin) {
		List<CommandPattern> list = new LinkedList<>();
		synchronized (patterns) {
			for (CommandPattern pattern : patterns)
				if (pattern.plugin == plugin)
					list.add(pattern);
			patterns.removeAll(list);
		}
	}
}