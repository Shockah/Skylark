package scommands;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import pl.shockah.SortedLinkedList;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandPattern {
	public static final double
		LOW_PRIORITY = 0d,
		MEDIUM_PRIORITY = 500d,
		HIGH_PRIORITY = 1000d;
	
	public final shocky3.Plugin plugin;
	public final double priority;
	public final List<CommandProvider> providers = Collections.synchronizedList(new SortedLinkedList<CommandProvider>(new Comparator<CommandProvider>(){
		public int compare(CommandProvider o1, CommandProvider o2) {
			return Double.compare(o2.priority, o1.priority);
		}
	}));
	
	public CommandPattern(shocky3.Plugin plugin, double priority) {
		this.plugin = plugin;
		this.priority = priority;
	}
	
	public void add(CommandProvider... providers) {
		synchronized (this.providers) {for (CommandProvider provider : providers)
			this.providers.add(provider);
		}
	}
	public void remove(CommandProvider... providers) {
		synchronized (this.providers) {for (CommandProvider provider : providers)
			this.providers.remove(provider);
		}
	}
	public void removeAll(shocky3.Plugin plugin) {
		List<CommandProvider> list = new LinkedList<>();
		synchronized (providers) {
			for (CommandProvider provider : providers)
				if (provider.plugin == plugin)
					list.add(provider);
			providers.removeAll(list);
		}
	}
	
	public abstract CommandPatternMatch match(GenericUserMessageEvent e);
	
	public CommandStackEntry matchCommand(GenericUserMessageEvent e) {
		CommandPatternMatch pmatch = match(e);
		if (pmatch == null)
			return null;
		
		List<CommandMatch> matches = new SortedLinkedList<>(new Comparator<CommandMatch>(){
			public int compare(CommandMatch o1, CommandMatch o2) {
				if (o1.perfectMatch == o2.perfectMatch)
					return Double.compare(o2.priority, o1.priority);
				else
					return o1.perfectMatch ? -1 : 1;
			}
		});
		synchronized (providers) {for (CommandProvider provider : providers) {
			CommandMatch match = provider.provide(e, pmatch.name, pmatch.input);
			if (match != null)
				matches.add(match);
		}}
		
		return matches.isEmpty() ? null : new CommandStackEntry(matches.get(0).command, pmatch.input);
	}
}