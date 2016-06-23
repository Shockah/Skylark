package io.shockah.skylark.factoids;

import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.factoids.db.Factoid;
import io.shockah.skylark.ident.IdentPlugin;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;

public class FactoidsPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency
	protected IdentPlugin identPlugin;
	
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	protected FactoidCommandProvider commandProvider;
	
	public FactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		getConfig().putDefault("defaultContext", Factoid.Context.Server.name());
		commandsPlugin.addProvider(commandProvider = new FactoidCommandProvider());
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeProvider(commandProvider);
	}
	
	public Factoid.Context getDefaultContext() {
		return Factoid.Context.valueOf(getConfig().getString("defaultContext"));
	}
}