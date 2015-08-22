package skylark.botadmin;

import skylark.PluginInfo;
import skylark.commands.Command;

public class Plugin extends skylark.ListenerPlugin {
	@Dependency
	protected static skylark.commands.Plugin commandsPlugin;
	
	protected Command
		joinCommand,
		partCommand;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		commandsPlugin.register(
			joinCommand = new JoinCommand(this),
			partCommand = new PartCommand(this)
		);
	}
	
	protected void onUnload() {
		commandsPlugin.unregister(
			joinCommand,
			partCommand
		);
	}
}