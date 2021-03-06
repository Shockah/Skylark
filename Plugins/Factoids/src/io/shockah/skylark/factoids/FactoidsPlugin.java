package io.shockah.skylark.factoids;

import java.util.HashMap;
import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.event.GenericUserMessageEvent;
import io.shockah.skylark.factoids.db.Factoid;
import io.shockah.skylark.ident.IdentPlugin;
import io.shockah.skylark.permissions.PermissionsPlugin;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteMap;

public class FactoidsPlugin extends Plugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	@Dependency
	protected IdentPlugin identPlugin;
	
	@Dependency
	protected PermissionsPlugin permissionsPlugin;
	
	protected FactoidCommandProvider commandProvider;
	
	protected ReadWriteMap<String, FactoidType> types = new ReadWriteMap<>(new HashMap<>());
	
	private RememberCommand rememberCommand;
	private ForgetCommand forgetCommand;
	private FactoidInfoCommand factoidInfoCommand;
	
	public FactoidsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		getConfig().putDefault("defaultContext", Factoid.Context.Server.name());
		commandsPlugin.addProvider(commandProvider = new FactoidCommandProvider(this));
		addType(new SimpleFactoidType());
		commandsPlugin.addNamedCommands(
			rememberCommand = new RememberCommand(this),
			forgetCommand = new ForgetCommand(this),
			factoidInfoCommand = new FactoidInfoCommand(this)
		);
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeProvider(commandProvider);
		commandsPlugin.removeNamedCommands(
			rememberCommand,
			forgetCommand,
			factoidInfoCommand
		);
	}
	
	public void addType(FactoidType type) {
		types.put(type.type, type);
	}
	
	public void removeType(FactoidType type) {
		types.remove(type.type);
	}
	
	public FactoidType getType(String type) {
		return types.get(type);
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
			where.equals(Factoid.NAME_COLUMN, name);
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