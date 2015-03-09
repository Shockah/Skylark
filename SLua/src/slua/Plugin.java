package slua;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static scommands.old.Plugin pluginCmd;
	@Dependency protected static sfactoids.Plugin pluginFactoids;
	
	public Lua lua;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		lua = new Lua(this);
		
		pluginCmd.provider.add(
			new CmdLua(this)
		);
		pluginFactoids.provider.builder.add(
			new LuaFactoidParser(this)
		);
	}
}