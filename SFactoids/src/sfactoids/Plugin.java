package sfactoids;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		scommands.Plugin pluginCmd = botApp.pluginManager.byInternalName("Shocky.SCommands");
		pluginCmd.add(new FactoidCommandProvider(this));
		pluginCmd.provider.add(
			new CmdRemember(this),
			new CmdForget(this),
			new CmdInfo(this)
		);
	}
}