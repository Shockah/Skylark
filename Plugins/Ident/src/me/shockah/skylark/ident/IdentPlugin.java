package me.shockah.skylark.ident;

import java.util.ArrayList;
import java.util.Map;
import me.shockah.skylark.Bot;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.ServerManager;
import me.shockah.skylark.plugin.BotManagerService;
import me.shockah.skylark.plugin.Plugin;
import me.shockah.skylark.plugin.PluginManager;
import me.shockah.skylark.util.ReadWriteList;
import org.pircbotx.User;

public class IdentPlugin extends Plugin implements BotManagerService.Factory {
	protected final ReadWriteList<IdentMethodFactory> methodFactories = new ReadWriteList<>(new ArrayList<>());
	
	public IdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		register(
			new NameIdentMethod.Factory(),
			new HostnameIdentMethod.Factory(),
			new ServerIdentMethod.Factory()
		);
	}
	
	public void register(IdentMethodFactory... factories) {
		methodFactories.writeOperation(methodFactories -> {
			for (IdentMethodFactory factory : factories) {
				methodFactories.add(factory);
				ServerManager serverManager = manager.app.serverManager;
				serverManager.botManagers.iterate(botManager -> {
					IdentService service = botManager.getService(IdentService.class);
					if (service != null) {
						service.methods.add(factory.create(service));
					}
				});
			}
		});
	}
	
	public void unregister(IdentMethodFactory... factories) {
		methodFactories.writeOperation(methodFactories -> {
			for (IdentMethodFactory factory : factories) {
				methodFactories.remove(factory);
				ServerManager serverManager = manager.app.serverManager;
				serverManager.botManagers.iterate(botManager -> {
					IdentService service = botManager.getService(IdentService.class);
					if (service != null)
						service.methods.remove(factory);
				});
			}
		});
	}

	@Override
	public BotManagerService createService(BotManager manager) {
		return new IdentService(this, manager);
	}
	
	public Map<IdentMethod, String> getIdentsForUser(User user) {
		Bot bot = user.getBot();
		return bot.manager.getService(IdentService.class).getIdentsForUser(user);
	}
}