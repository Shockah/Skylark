package me.shockah.skylark.ident;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.shockah.skylark.Bot;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.ServerManager;
import me.shockah.skylark.plugin.BotManagerService;
import me.shockah.skylark.plugin.ListenerPlugin;
import me.shockah.skylark.plugin.PluginManager;
import org.pircbotx.User;

public class IdentPlugin extends ListenerPlugin implements BotManagerService.Factory {
	protected final Map<String, IdentMethodFactory> methods = Collections.synchronizedMap(new HashMap<>());
	
	public IdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	public void register(IdentMethodFactory... factories) {
		synchronized (methods) {
			for (IdentMethodFactory factory : factories) {
				methods.put(factory.prefix, factory);
				ServerManager serverManager = manager.app.serverManager;
				synchronized (serverManager.botManagers) {
					for (BotManager botManager : serverManager.botManagers) {
						IdentService service = botManager.getService(IdentService.class);
						if (service != null) {
							IdentMethod method = factory.create(service);
							service.methods.put(method.prefix, method);
						}
					}
				}
			}
		}
	}
	
	public void unregister(IdentMethodFactory... factories) {
		synchronized (methods) {
			for (IdentMethodFactory factory : factories) {
				methods.remove(factory.prefix);
				ServerManager serverManager = manager.app.serverManager;
				synchronized (serverManager.botManagers) {
					for (BotManager botManager : serverManager.botManagers) {
						IdentService service = botManager.getService(IdentService.class);
						if (service != null)
							service.methods.remove(factory.prefix);
					}
				}
			}
		}
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