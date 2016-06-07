package me.shockah.skylark.ident;

import java.util.HashMap;
import java.util.Map;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.plugin.BotManagerService;
import me.shockah.skylark.util.ReadWriteMap;
import org.pircbotx.User;

public final class IdentService extends BotManagerService {
	public final IdentPlugin plugin;
	protected final ReadWriteMap<String, IdentMethod> methods = new ReadWriteMap<>(new HashMap<>());
	
	public IdentService(IdentPlugin plugin, BotManager manager) {
		super(manager);
		this.plugin = plugin;
		
		plugin.methods.iterateValues(factory -> {
			IdentMethod method = factory.create(this);
			methods.put(method.prefix, method);
		});
	}
	
	public Map<IdentMethod, String> getIdentsForUser(User user) {
		Map<IdentMethod, String> results = new HashMap<>();
		methods.iterateValues(method -> {
			if (method.isAvailable())
				results.put(method, method.getForUser(user));
		});
		return results;
	}
	
	public IdentMethod getMethod(String prefix) {
		return methods.get(prefix);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IdentMethod> T getMethod(Class<T> clazz) {
		return (T)methods.findOne(method -> clazz.isInstance(method));
	}
}