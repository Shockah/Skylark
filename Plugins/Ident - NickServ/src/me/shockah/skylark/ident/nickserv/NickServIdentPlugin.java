package me.shockah.skylark.ident.nickserv;

import me.shockah.skylark.ident.IdentPlugin;
import me.shockah.skylark.plugin.ListenerPlugin;
import me.shockah.skylark.plugin.PluginManager;

public class NickServIdentPlugin extends ListenerPlugin {
	@Dependency
	protected IdentPlugin identPlugin;
	
	public NickServIdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
}