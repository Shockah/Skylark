package scommands;

import java.util.List;
import pl.shockah.Pair;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public abstract class CommandProvider {
	public static enum EPriority {
		Low(1), Medium(2), High(3);
		
		public final int value;
		
		private EPriority(int value) {
			this.value = value;
		}
	}
	
	public final shocky3.Plugin plugin;
	
	public CommandProvider(shocky3.Plugin plugin) {
		this.plugin = plugin;
	}
	
	public abstract void provide(List<Pair<ICommand, EPriority>> candidates, GenericUserMessageEvent e, String trigger, String args, CommandResult result);
}