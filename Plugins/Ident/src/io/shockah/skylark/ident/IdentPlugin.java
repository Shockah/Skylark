package io.shockah.skylark.ident;

import io.shockah.skylark.Bot;
import io.shockah.skylark.BotManager;
import io.shockah.skylark.ServerManager;
import io.shockah.skylark.plugin.BotManagerService;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteList;
import java.util.ArrayList;
import java.util.Map;
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
					if (service != null)
						service.methods.add(factory.create(service));
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
	
	public IdentMethodFactory getFactoryForPrefix(String prefix) {
		return methodFactories.filterFirst(factory -> factory.prefix.equalsIgnoreCase(prefix));
	}

	@Override
	public BotManagerService createService(BotManager manager) {
		return new IdentService(this, manager);
	}
	
	public Map<IdentMethod, String> getIdentsForUser(User user) {
		return user.<Bot>getBot().manager.getService(IdentService.class).getIdentsForUser(user);
	}
}