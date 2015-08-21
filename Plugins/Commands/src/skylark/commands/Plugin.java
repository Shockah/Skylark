package skylark.commands;

import java.util.List;
import org.pircbotx.hooks.events.MessageEvent;
import skylark.PluginInfo;
import skylark.pircbotx.event.GenericUserMessageEvent;
import skylark.util.Synced;

public class Plugin extends skylark.ListenerPlugin {
	@Dependency
	protected static skylark.privileges.Plugin privilegesPlugin;
	@Dependency
	protected static skylark.settings.Plugin settingsPlugin;
	
	protected final List<CommandPattern> patterns = Synced.list();
	protected final List<CommandProvider> providers = Synced.list();
	
	protected DefaultCommandProvider provider;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		register(
			new DefaultCommandPattern(this)
		);
		
		register(
			provider = new DefaultCommandProvider(this)
		);
	}
	
	protected void onUnload() {
		patterns.clear();
		providers.clear();
	}
	
	public void register(CommandProvider provider) {
		providers.add(provider);
	}
	
	public void register(CommandProvider... providers) {
		for (CommandProvider provider : providers)
			register(provider);
	}
	
	public void unregister(CommandProvider provider) {
		providers.remove(provider);
	}
	
	public void unregister(CommandProvider... providers) {
		for (CommandProvider provider : providers)
			unregister(provider);
	}
	
	public void register(CommandPattern pattern) {
		patterns.add(pattern);
	}
	
	public void register(CommandPattern... patterns) {
		for (CommandPattern pattern : patterns)
			register(pattern);
	}
	
	public void unregister(CommandPattern pattern) {
		patterns.remove(pattern);
	}
	
	public void unregister(CommandPattern... patterns) {
		for (CommandPattern pattern : patterns)
			unregister(pattern);
	}
	
	public void register(Command command) {
		provider.register(command);
	}
	
	public void register(Command... commands) {
		provider.register(commands);
	}
	
	public void unregister(Command command) {
		provider.unregister(command);
	}
	
	public void unregister(Command... commands) {
		provider.unregister(commands);
	}
	
	protected void onMessage(MessageEvent e) {
		triggerCommand(new GenericUserMessageEvent(e));
	}
	
	public void triggerCommand(GenericUserMessageEvent e) {
		CommandMatch match = null;
		synchronized (patterns) {
			for (CommandPattern pattern : patterns) {
				match = pattern.match(e);
				if (match != null)
					break;
			}
		}
		if (match == null)
			return;
		
		Command command = null;
		synchronized (providers) {
			for (CommandProvider provider : providers) {
				command = provider.provide(e, match.command);
				if (command != null)
					break;
			}
		}
		if (command == null)
			return;
		
		CommandStack stack = new CommandStack(e);
		CommandResult result = stack.execute(command, match.args);
		outputCommand(e, result.text);
	}
	
	protected void outputCommand(GenericUserMessageEvent e, String text) {
		String[] lines = text.split("\\r?\\n");
		for (String line : lines) {
			if (e.getChannel() == null)
				e.getUser().send().message(line);
			else
				e.getChannel().send().message(line);
		}
	}
}