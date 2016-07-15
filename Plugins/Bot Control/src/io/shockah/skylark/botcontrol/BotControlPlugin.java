package io.shockah.skylark.botcontrol;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.hooks.events.InviteEvent;
import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;

public class BotControlPlugin extends ListenerPlugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	private JoinCommand joinCommand;
	private PartCommand partCommand;
	private ReloadModulesCommand reloadModulesCommand;
	
	public BotControlPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		commandsPlugin.addNamedCommands(
			joinCommand = new JoinCommand(this),
			partCommand = new PartCommand(this),
			reloadModulesCommand = new ReloadModulesCommand()
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommands(
			joinCommand,
			partCommand,
			reloadModulesCommand
		);
	}
	
	public JoinCommand getJoinCommand() {
		return joinCommand;
	}
	
	public PartCommand getPartCommand() {
		return partCommand;
	}
	
	@Override
	protected void onInvite(InviteEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		Bot foundBot = manager.bots.firstResult(bot -> {
			for (Channel channel : bot.getUserBot().getChannels()) {
				if (channel.getName().equals(e.getChannel()))
					return bot;
			}
			return null;
		});
		
		if (foundBot == null)
			return;
		
		manager.joinChannel(e.getChannel());
	}
	
	protected boolean permissionGranted(User user, String subpermission) {
		return permissionsPlugin.permissionGranted(user, String.format("%s.%s", info.packageName(), subpermission));
	}
}