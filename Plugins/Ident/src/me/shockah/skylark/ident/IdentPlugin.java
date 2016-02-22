package me.shockah.skylark.ident;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import me.shockah.skylark.BotManager;
import me.shockah.skylark.plugin.BotManagerService;
import me.shockah.skylark.plugin.ListenerPlugin;
import me.shockah.skylark.plugin.PluginManager;

public class IdentPlugin extends ListenerPlugin implements BotManagerService.Factory {
	protected final Map<String, IdentMethodFactory> methods = Collections.synchronizedMap(new HashMap<>());
	
	public IdentPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	public void register(IdentMethodFactory... factories) {
		synchronized (methods) {
			for (IdentMethodFactory factory : factories) {
				methods.put(factory.prefix, factory);
				//TODO: find already existing services; create instances
			}
		}
	}
	
	public void unregister(IdentMethodFactory... factories) {
		synchronized (methods) {
			for (IdentMethodFactory factory : factories) {
				methods.remove(factory.prefix);
				//TODO: find already existing services; remove instances
			}
		}
	}

	@Override
	public BotManagerService createService(BotManager manager) {
		return new IdentService(this, manager);
	}
}