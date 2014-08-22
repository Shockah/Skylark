package scommands;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.events.MessageEvent;
import shocky3.Shocky;

public abstract class Command {
	public final String main;
	public final List<String> alt = Collections.synchronizedList(new LinkedList<String>());
	
	public Command(String main, String... alts) {
		this.main = main.toLowerCase();
		for (String alt : alts) this.alt.add(alt.toLowerCase());
	}
	
	public abstract void call(Shocky botApp, MessageEvent<PircBotX> e, String trigger, String args);
}