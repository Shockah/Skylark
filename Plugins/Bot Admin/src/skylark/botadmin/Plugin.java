package skylark.botadmin;

import org.pircbotx.hooks.events.InviteEvent;
import skylark.BotManager;
import skylark.PluginInfo;
import skylark.pircbotx.Bot;

public class Plugin extends skylark.ListenerPlugin {
	@Dependency
	protected static skylark.commands.Plugin commandsPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		commandsPlugin.register(
			new JoinCommand(this),
			new PartCommand(this)
		);
	}
	
	protected void onUnload() {
		commandsPlugin.unregister(this);
	}
	
	protected void onInvite(InviteEvent e) {
		BotManager manager = e.<Bot>getBot().manager;
		Bot bot = manager.botForChannel(e.getChannel());
		if (bot == null)
			manager.joinChannel(e.getChannel());
	}
}