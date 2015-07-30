package shocky3.ident;

import java.util.LinkedHashMap;
import java.util.Map;
import org.pircbotx.User;
import shocky3.BotManager;
import shocky3.PluginInfo;
import shocky3.pircbotx.Bot;
import shocky3.util.Synced;

public class Plugin extends shocky3.Plugin {
	protected final Map<String, IdentMethodFactory> identMethodFactories = Synced.map(new LinkedHashMap<>());
	protected final Map<BotManager, Map<String, IdentMethod>> identMethods = Synced.map();
	
	public Plugin(PluginInfo pinfo) {
		super(pinfo);
	}
	
	protected void onLoad() {
		register(
			new IdentMethodFactory.Delegate("nick", "Nick",
				(factory, manager) -> new IdentMethod.Delegate(manager, factory, IdentMethod.CREDIBILITY_MEDIUM_LOW,
					user -> user.getNick())),
			new IdentMethodFactory.Delegate("host", "Hostname",
				(factory, manager) -> new IdentMethod.Delegate(manager, factory, IdentMethod.CREDIBILITY_MEDIUM_LOW,
					user -> user.getHostname())),
			new IdentMethodFactory.Delegate("srv", "Server",
				(factory, manager) -> new IdentMethod.Delegate(manager, factory, IdentMethod.CREDIBILITY_LOW,
					user -> user.getBot().getServerHostname()))
		);
	}
	
	protected void onUnload() {
		identMethodFactories.clear();
	}
	
	public void register(IdentMethodFactory factory) {
		identMethodFactories.put(factory.id, factory);
	}
	
	public void register(IdentMethodFactory... factories) {
		for (IdentMethodFactory factory : factories)
			register(factory);
	}
	
	public void unregister(IdentMethodFactory factory) {
		identMethodFactories.remove(factory.id);
	}
	
	public void unregister(IdentMethodFactory... factories) {
		for (IdentMethodFactory factory : factories)
			unregister(factory);
	}
	
	protected void prepareForManager(BotManager manager) {
		synchronized (identMethods) {
			if (identMethods.containsKey(manager))
				return;
			createForManager(manager);
		}
	}
	
	protected void createForManager(BotManager manager) {
		synchronized (identMethods) {
			Map<String, IdentMethod> methods = Synced.map(new LinkedHashMap<>());
			Synced.forEach(identMethodFactories, factory -> {
				methods.put(factory.id, factory.create(manager));
			});
			identMethods.put(manager, methods);
		}
	}
	
	protected void destroyForManager(BotManager manager) {
		identMethods.remove(manager);
	}
	
	public IdentMethodFactory getForID(String id) {
		synchronized (identMethodFactories) {
			return identMethodFactories.containsKey(id) ? identMethodFactories.get(id) : null;
		}
	}
	
	public IdentMethod getForID(BotManager manager, String id) {
		synchronized (identMethods) {
			prepareForManager(manager);
			Map<String, IdentMethod> methods = identMethods.get(manager);
			return methods.containsKey(id) ? methods.get(id) : null;
		}
	}
	
	public IdentMethodFactory getForFullIdent(String ident) {
		int index = ident.indexOf(':');
		if (index == -1)
			return null;
		String id = ident.substring(0, index);
		//ident = ident.substring(index + 1);
		return getForID(id);
	}
	
	public IdentMethod getForFullIdent(BotManager manager, String ident) {
		int index = ident.indexOf(':');
		if (index == -1)
			return null;
		String id = ident.substring(0, index);
		//ident = ident.substring(index + 1);
		return getForID(manager, id);
	}
	
	public boolean matches(User user, String ident) {
		Bot bot = (Bot)user.getBot();
		IdentMethod method = null;
		synchronized (identMethods) {
			prepareForManager(bot.manager);
			method = getForFullIdent(bot.manager, ident);
		}
		if (method == null)
			return false;
		return method.getFullIdentFor(user).equals(ident);
	}
}