package sbotcontrol;

import org.pircbotx.hooks.events.InviteEvent;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;

public class Plugin extends shocky3.ListenerPlugin {
	@Dependency public static sident.Plugin pluginIdent;
	@Dependency(internalName = "Shocky.Commands") protected static shocky3.Plugin pluginCmd;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void postLoad() {
		if (pluginCmd != null) {
			scommands.Plugin pluginCmd = (scommands.Plugin)Plugin.pluginCmd;
			pluginCmd.provider.add(
				new sbotcontrol.scommands.CmdDie(this),
				new sbotcontrol.scommands.CmdJoin(this),
				new sbotcontrol.scommands.CmdPart(this)
			);
		}
	}
	
	protected void onInvite(InviteEvent<Bot> e) {
		e.getBot().manager.joinChannel(e.getChannel());
	}
}