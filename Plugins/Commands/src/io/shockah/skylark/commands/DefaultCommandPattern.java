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
	
	@SuppressWarnings("unchecked")
	@Override
	public CommandPreparedCall<String, ?> provide(GenericUserMessageEvent e) {
		String message = e.getMessage();
		for (String prefix : prefixes) {
			if (message.startsWith(prefix) && message.length() > prefix.length()) {
				String[] spl = message.split("\\s");
				String commandList = spl[0].substring(prefix.length());
				String input = message.substring(prefix.length() + commandList.length() + 1);
				
				String[] commandNames = commandList.split(">");
				if (commandNames.length == 1) {
					Command<String, Object> command = (Command<String, Object>)providers.firstResult(provider -> provider.provide(commandNames[0]));
					return new CommandPreparedCall<String, Object>(command, input);
				} else {
					Command<?, ?>[] commands = new Command[commandNames.length];
					for (int i = 0; i < commandNames.length; i++) {
						String commandName = commandNames[i];
						Command<?, ?> command = providers.firstResult(provider -> provider.provide(commandName));
						if (command == null)
							return null;
						commands[i] = command;
					}
					return new CommandPreparedCall<String, Object>(new ChainCommand<String, Object>(commands), input);
				}
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
	
	public NamedCommand<?, ?> findCommand(String name) {
		return providers.firstResult(provider -> provider.provide(name));
	}
}