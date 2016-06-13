package io.shockah.skylark.botcontrol;

import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;

public class BotControlPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	private JoinCommand joinCommand;
	
	public BotControlPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommand(joinCommand = new JoinCommand());
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommand(joinCommand);
	}
	
	public JoinCommand getJoinCommand() {
		return joinCommand;
	}
}