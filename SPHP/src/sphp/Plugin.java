package sphp;

import shocky3.PluginInfo;

public class Plugin extends shocky3.Plugin {
	@Dependency protected static scommands.old.Plugin pluginCmd;
	@Dependency protected static sfactoids.Plugin pluginFactoids;
	
	public PHP php;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		botApp.settings.add(this, "url", "http://localhost/shocky/shocky.php");
		php = new PHP(this);
		
		pluginCmd.provider.add(
			new CmdPHP(this)
		);
		pluginFactoids.provider.builder.add(
			new PHPFactoidParser(this)
		);
	}
}