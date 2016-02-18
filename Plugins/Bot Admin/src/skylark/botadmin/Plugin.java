package skylark.botadmin;

import org.pircbotx.hooks.events.InviteEvent;
import skylark.old.BotManager;
import skylark.old.PluginInfo;
import skylark.old.pircbotx.Bot;

public class Plugin extends skylark.old.ListenerPlugin {
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