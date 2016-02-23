package me.shockah.skylark.ident;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.plugin.BotManagerService;
import org.pircbotx.User;

public final class IdentService extends BotManagerService {
	public final IdentPlugin plugin;
	protected final Map<String, IdentMethod> methods = Collections.synchronizedMap(new HashMap<>());
	
	public IdentService(IdentPlugin plugin, BotManager manager) {
		super(manager);
		this.plugin = plugin;
		
		synchronized (plugin.methods) {
			for (IdentMethodFactory factory : plugin.methods.values()) {
				IdentMethod method = factory.create(this);
				methods.put(method.prefix, method);
			}
		}
	}
	
	public Map<IdentMethod, String> getIdentsForUser(User user) {
		Map<IdentMethod, String> results = new HashMap<>();
		synchronized (methods) {
			for (IdentMethod method : methods.values()) {
				if (method.isAvailable())
					results.put(method, method.getForUser(user));
			}
		}
		return results;
	}
	
	public IdentMethod getMethod(String prefix) {
		return methods.get(prefix);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IdentMethod> T getMethod(Class<T> clazz) {
		synchronized (methods) {
			for (IdentMethod method : methods.values()) {
				if (clazz.isInstance(method))
					return (T)method;
			}
		}
		return null;
	}
}