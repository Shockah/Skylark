package skylark.commands;

import java.util.Map;
import skylark.Plugin;
import skylark.pircbotx.event.GenericUserMessageEvent;
import skylark.util.Synced;

public class DefaultCommandProvider extends CommandProvider {
	protected final Map<String, Command> commands = Synced.map();
	
	public DefaultCommandProvider(Plugin plugin) {
		super(plugin);
	}
	
	public void register(Command command) {
		commands.put(command.name.toLowerCase(), command);
	}
	
	public void register(Command... commands) {
		for (Command command : commands)
			register(command);
	}
	
	public void unregister(Command command) {
		commands.remove(command.name.toLowerCase());
	}
	
	public void unregister(Command... commands) {
		for (Command command : commands)
			unregister(command);
	}
	
	public Command provide(GenericUserMessageEvent e, String name) {
		return commands.get(name.toLowerCase());
	}
}