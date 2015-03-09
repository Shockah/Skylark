package sfactoids;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static sident.Plugin pluginIdent;
	@Dependency protected static scommands.old.Plugin pluginCmd;
	
	public FactoidCommandProvider provider;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		pluginCmd.add(provider = new FactoidCommandProvider(this));
		pluginCmd.provider.add(
			new CmdRemember(this),
			new CmdForget(this),
			new CmdInfo(this)
		);
	}
}