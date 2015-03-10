package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class Command {
	public final shocky3.Plugin plugin;
	public final String main;
	public final List<String> alts = Collections.synchronizedList(new LinkedList<String>());
	
	public Command(shocky3.Plugin plugin, String main, String... alts) {
		this.plugin = plugin;
		this.main = main.toLowerCase();
		for (String alt : alts)
			this.alts.add(alt.toLowerCase());
	}
	
	public abstract String call(GenericUserMessageEvent e, String input, CommandStack stack);
}