package io.shockah.skylark.botcontrol;

import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.InviteEvent;

public class BotControlPlugin extends ListenerPlugin {
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
		commandsPlugin.addNamedCommand(joinCommand = new JoinCommand(this));
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommand(joinCommand);
	}
	
	public JoinCommand getJoinCommand() {
		return joinCommand;
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
}