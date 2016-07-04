package io.shockah.skylark.factoids;

import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.event.GenericUserMessageEvent;
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
	
	public Factoid findActiveFactoid(GenericUserMessageEvent e, String name) {
		Factoid factoid;
		
		factoid = findActiveFactoid(e, name, Factoid.Context.Channel);
		if (factoid != null)
			return factoid;
		
		factoid = findActiveFactoid(e, name, Factoid.Context.Server);
		if (factoid != null)
			return factoid;
		
		return findActiveFactoid(e, name, Factoid.Context.Global);
	}
	
	public Factoid findActiveFactoid(GenericUserMessageEvent e, String name, Factoid.Context context) {
		return manager.app.databaseManager.queryFirst(Factoid.class, (builder, where) -> {
			where.equals(Factoid.ACTIVE_COLUMN, true);
			if (context != null)
				where.equals(Factoid.CONTEXT_COLUMN, context);
			switch (context) {
				case Channel:
					where.equals(Factoid.CHANNEL_COLUMN, e.getChannel().getName());
					where.equals(Factoid.SERVER_COLUMN, e.<Bot>getBot().manager.name);
					break;
				case Server:
					where.equals(Factoid.SERVER_COLUMN, e.<Bot>getBot().manager.name);
					break;
				default:
					break;
			}
			builder.orderBy(DbObject.ID_COLUMN, false);
		});
	}
}