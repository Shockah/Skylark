package scalc;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static scommands.Plugin pluginCmd;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginCmd.provider.add(new CmdCalc(this));
	}
}