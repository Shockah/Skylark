package io.shockah.skylark;

import io.shockah.skylark.plugin.BotService;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.ReadWriteList;
import java.util.ArrayList;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class Bot extends PircBotX {
	public final BotManager manager;
	public final WhoisManager whoisManager;
	
	public final ReadWriteList<BotService> services = new ReadWriteList<>(new ArrayList<>());
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		this.manager = manager;
		setupServices();
		
		whoisManager = new WhoisManager(this);
	}
	
	public void setupServices() {
		services.writeOperation(services -> {
			PluginManager pluginManager = manager.serverManager.app.pluginManager;
			pluginManager.botServiceFactories.iterate(factory -> {
				BotService service = factory.createService(this);
				services.add(service);
				pluginManager.botServices.add(service);
			});
		});
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BotService> T getService(Class<T> clazz) {
		return (T)services.filterFirst(service -> clazz.isInstance(service));
	}
}