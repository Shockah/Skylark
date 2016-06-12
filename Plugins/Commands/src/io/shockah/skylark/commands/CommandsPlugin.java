package io.shockah.skylark.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.pircbotx.hooks.events.ActionEvent;
import io.shockah.json.JSONObject;
import io.shockah.skylark.Bot;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteList;

public class CommandsPlugin extends ListenerPlugin {
	protected ReadWriteList<CommandPattern> patterns = new ReadWriteList<>(new ArrayList<>());
	
	protected DefaultCommandPattern defaultPattern;
	protected DefaultCommandProvider defaultProvider;
	
	public CommandsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		JSONObject config = manager.app.config.getObjectOrEmpty(info.packageName());
		defaultPattern = new DefaultCommandPattern(config.getList("prefixes").ofStrings().toArray(new String[0]));
		defaultProvider = new DefaultCommandProvider();
		defaultPattern.addProvider(defaultProvider);
	}
	
	public DefaultCommandPattern getDefaultPattern() {
		return defaultPattern;
	}
	
	public DefaultCommandProvider getDefaultProvider() {
		return defaultProvider;
	}
	
	public void addPattern(CommandPattern pattern) {
		patterns.add(pattern);
	}
	
	public void removePattern(CommandPattern pattern) {
		patterns.remove(pattern);
	}
	
	public void addProvider(CommandProvider provider) {
		defaultPattern.addProvider(provider);
	}
	
	public void removeProvider(CommandProvider provider) {
		defaultPattern.removeProvider(provider);
	}
	
	public void addNamedCommand(NamedCommand<?, ?> command) {
		defaultProvider.addNamedCommand(command);
	}
	
	public void removeNamedCommand(NamedCommand<?, ?> command) {
		defaultProvider.removeNamedCommand(command);
	}
	
	public CommandPreparedCall<String, ?> findCommandToCall(GenericUserMessageEvent e) {
		return patterns.firstResult(pattern -> pattern.provide(e));
	}
	
	public NamedCommand<?, ?> findCommand(String name) {
		return defaultPattern.findCommand(name);
	}
	
	@Override
	protected void onGenericUserMessage(GenericUserMessageEvent e) {
		if (e.getEvent() instanceof ActionEvent)
			return;
		
		CommandPreparedCall<String, ?> preparedCall = findCommandToCall(e);
		if (preparedCall == null)
			return;
		
		CommandCall call = new CommandCall(e);
		CommandValue<?> value = preparedCall.call(call);
		String output = value.toIRCOutput();
		
		if (output != null) {
			List<String> lines = Arrays.asList(output.split("\\r?\\n|\\r"));
			call.respond(e.<Bot>getBot().manager.linebreakIfNeeded(lines, preparedCall.getLineLimit(call)));
		}
	}
}