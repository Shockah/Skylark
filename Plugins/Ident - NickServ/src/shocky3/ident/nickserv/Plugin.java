package shocky3.ident.nickserv;

import shocky3.PluginInfo;
import shocky3.ident.IdentMethodFactory;

public class Plugin extends shocky3.Plugin {
	@Dependency
	protected shocky3.ident.Plugin identPlugin;
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		identPlugin.register(
			new IdentMethodFactory.Delegate("nickserv", "NickServ account",
				(factory, manager) -> new NickServIdentMethod(manager, factory))
		);
	}
}