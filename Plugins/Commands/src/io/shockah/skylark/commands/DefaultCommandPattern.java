package io.shockah.skylark.commands;

import java.util.ArrayList;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.util.ReadWriteList;

public class DefaultCommandPattern extends CommandPattern {
	private final String[] prefixes;
	private final ReadWriteList<CommandProvider> providers = new ReadWriteList<>(new ArrayList<>());
	
	public DefaultCommandPattern(String[] prefixes) {
		this.prefixes = prefixes;
	}
	
	@Override
	public Command provide(GenericUserMessageEvent e) {
		String message = e.getMessage();
		for (String prefix : prefixes) {
			if (message.startsWith(prefix) && message.length() > prefix.length()) {
				String[] spl = message.split("\\s");
				String commandName = spl[0].substring(prefix.length());
				String input = message.substring(prefix.length() + commandName.length() + 1);
				return providers.firstResult(provider -> provider.provide(e, commandName, input));
			}
		}
		return null;
	}
	
	public void addProvider(CommandProvider provider) {
		providers.add(provider);
	}
	
	public void removeProvider(CommandProvider provider) {
		providers.remove(provider);
	}
}