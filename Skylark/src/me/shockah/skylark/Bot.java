package me.shockah.skylark;

import java.util.ArrayList;
import me.shockah.skylark.plugin.BotService;
import me.shockah.skylark.plugin.PluginManager;
import me.shockah.skylark.util.ReadWriteList;
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
		return (T)services.findOne(service -> clazz.isInstance(service));
	}
	
	/*public boolean hasEnabledCapability(String capability) {
		for (String cap : getEnabledCapabilities()) {
			if (cap.equals(capability) || cap.startsWith(capability + "="))
				return true;
		}
		return false;
	}
	
	public String getEnabledCapabilityValue(String capability) {
		for (String cap : getEnabledCapabilities()) {
			if (cap.startsWith(capability + "=")) {
				String value = cap.substring(cap.indexOf('=') + 1);
				return value;
			}
		}
		return null;
	}
	
	public Integer getEnabledCapabilityIntValue(String capability) {
		String value = getEnabledCapabilityValue(capability);
		if (value != null) {
			try {
				return Integer.parseInt(value);
			} catch (Exception e) {
			}
		}
		return null;
	}*/
}