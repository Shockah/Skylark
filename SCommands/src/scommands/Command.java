package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public abstract class Command implements ICommand {
	public final shocky3.Plugin plugin;
	public final String main;
	public final List<String> alt = Collections.synchronizedList(new LinkedList<String>());
	
	public Command(shocky3.Plugin plugin, String main, String... alts) {
		this.plugin = plugin;
		this.main = main.toLowerCase();
		for (String alt : alts) this.alt.add(alt.toLowerCase());
	}
}