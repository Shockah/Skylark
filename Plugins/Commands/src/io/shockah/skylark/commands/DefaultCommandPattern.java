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
	public PreparedCommandCall<?, ?> provide(GenericUserMessageEvent e) throws CommandParseException {
		String message = e.getMessage();
		for (String prefix : prefixes) {
			if (message.startsWith(prefix) && message.length() > prefix.length()) {
				String[] spl = message.split("\\s");
				String commandList = spl[0].substring(prefix.length());
				String textInput = message.substring(Math.min(prefix.length() + commandList.length() + 1, message.length()));
				
				String[] commandNames = commandList.split(">");
				if (commandNames.length == 1) {
					Command<Object, Object> command = (Command<Object, Object>)providers.firstResult(provider -> provider.provide(e, commandNames[0]));
					if (command == null)
						return null;
					return new PreparedCommandCall<>(command, command.parseInput(e, textInput));
				} else {
					Command<?, ?>[] commands = new Command[commandNames.length];
					for (int i = 0; i < commandNames.length; i++) {
						String commandName = commandNames[i];
						Command<?, ?> command = providers.firstResult(provider -> provider.provide(e, commandName));
						if (command == null)
							return null;
						commands[i] = command;
					}
					Command<Object, Object> command = new ChainCommand<>(commands);
					return new PreparedCommandCall<>(command, command.parseInput(e, textInput));
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
	
	public NamedCommand<?, ?> findCommand(GenericUserMessageEvent e, String name) {
		return providers.firstResult(provider -> provider.provide(e, name));
	}
}