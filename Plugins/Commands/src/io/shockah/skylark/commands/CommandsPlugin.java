package io.shockah.skylark.commands;

import java.util.ArrayList;
import io.shockah.json.JSONObject;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteList;

public class CommandsPlugin extends Plugin {
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
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
	
	public void addNamedCommand(NamedCommand command) {
		defaultProvider.addCommand(command);
	}
	
	public void removeNamedCommand(NamedCommand command) {
		defaultProvider.removeCommand(command);
	}
	
	public Command findCommand(GenericUserMessageEvent e) {
		return patterns.firstResult(pattern -> pattern.provide(e));
	}
}