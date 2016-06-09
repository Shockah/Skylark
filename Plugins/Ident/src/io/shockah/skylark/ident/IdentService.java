package io.shockah.skylark.ident;

import io.shockah.skylark.BotManager;
import io.shockah.skylark.plugin.BotManagerService;
import io.shockah.skylark.util.ReadWriteList;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.pircbotx.User;

public final class IdentService extends BotManagerService {
	public final IdentPlugin plugin;
	protected final ReadWriteList<IdentMethod> methods = new ReadWriteList<>(new ArrayList<>());
	
	public IdentService(IdentPlugin plugin, BotManager manager) {
		super(manager);
		this.plugin = plugin;
		
		plugin.methodFactories.iterate(factory -> {
			methods.add(factory.create(this));
		});
	}
	
	public Map<IdentMethod, String> getIdentsForUser(User user) {
		Map<IdentMethod, String> results = new HashMap<>();
		methods.iterate(method -> {
			if (method.isAvailable())
				results.put(method, method.getForUser(user));
		});
		return results;
	}
	
	public IdentMethod getMethod(String prefix) {
		return methods.findOne(method -> method.prefix.equals(prefix));
	}
	
	public IdentMethod getMethod(IdentMethodFactory factory) {
		return methods.findOne(method -> method.factory == factory);
	}
	
	@SuppressWarnings("unchecked")
	public <T extends IdentMethod> T getMethod(Class<T> clazz) {
		return (T)methods.findOne(method -> clazz.isInstance(method));
	}
}