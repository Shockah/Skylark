package skylark.commands;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.pircbotx.hooks.events.MessageEvent;
import pl.shockah.SortedArrayList;
import skylark.PluginInfo;
import skylark.pircbotx.event.GenericUserMessageEvent;
import skylark.util.Synced;

public class Plugin extends skylark.ListenerPlugin {
	@Dependency
	protected static skylark.privileges.Plugin privilegesPlugin;
	@Dependency
	protected static skylark.settings.Plugin settingsPlugin;
	
	protected final List<CommandPattern> patterns = Synced.list();
	protected final List<CommandProvider> providers = Synced.list(new SortedArrayList<CommandProvider>((i1, i2) -> {
		return i1.priority == i2.priority ? 0 : (i1.priority > i2.priority ? -1 : 1);
	}));
	
	protected DefaultCommandProvider provider;
	
	protected CommandInputParser
		simpleParser,
		splitParser,
		complexParser;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		simpleParser = new SimpleCommandInputParser();
		splitParser = new SplitCommandInputParser();
		complexParser = new ComplexCommandInputParser();
		
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
		
		simpleParser = null;
		splitParser = null;
		complexParser = null;
	}
	
	public CommandInputParser getSimpleCommandInputParser() {
		return simpleParser;
	}
	
	public CommandInputParser getSplitCommandInputParser() {
		return splitParser;
	}
	
	public CommandInputParser getComplexCommandInputParser() {
		return complexParser;
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
	
	public void unregister(skylark.Plugin plugin) {
		Synced.iterate(patterns, (pattern, ith) -> {
			if (pattern.plugin == plugin)
				ith.remove();
		});
		Synced.iterate(providers, (provider, ith) -> {
			if (provider.plugin == plugin)
				ith.remove();
		});
		synchronized (provider.commands) {
			Iterator<Map.Entry<String, Command>> it = provider.commands.entrySet().iterator();
			while (it.hasNext())
				if (it.next().getValue().plugin == plugin)
					it.remove();
		}
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
		CommandOutput output = stack.execute(command, match.args);
		outputCommand(e, output.text);
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