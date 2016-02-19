package skylark.commands;

import java.util.Map;
import me.shockah.skylark.event.GenericUserMessageEvent;
import skylark.old.Plugin;
import skylark.old.util.Synced;

public class DefaultCommandProvider extends CommandProvider {
	protected final Map<String, Command> commands = Synced.map();
	
	public DefaultCommandProvider(Plugin plugin) {
		super(plugin, PRIORITY_MEDIUM);
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