package me.shockah.skylark.ident.nickserv;

import me.shockah.skylark.ident.IdentMethodFactory;
import me.shockah.skylark.ident.IdentPlugin;
import me.shockah.skylark.plugin.ListenerPlugin;
import me.shockah.skylark.plugin.PluginManager;

public class NickServIdentPlugin extends ListenerPlugin {
	@Dependency
	protected IdentPlugin identPlugin;
	
	private IdentMethodFactory factory;
	
	public NickServIdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		identPlugin.register(factory = new NickServIdentMethod.Factory());
	}
	
	@Override
	protected void onUnload() {
		identPlugin.unregister(factory);
	}
	
	public IdentMethodFactory getIdentMethodFactory() {
		return factory;
	}
}