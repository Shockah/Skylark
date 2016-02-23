package me.shockah.skylark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import me.shockah.skylark.plugin.BotService;
import me.shockah.skylark.plugin.PluginManager;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class Bot extends PircBotX {
	public final BotManager manager;
	public final WhoisManager whoisManager;
	
	public final List<BotService> services = Collections.synchronizedList(new ArrayList<>());
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		this.manager = manager;
		setupServices();
		
		whoisManager = new WhoisManager(this);
	}
	
	public void setupServices() {
		synchronized (services) {
			PluginManager pluginManager = manager.serverManager.app.pluginManager;
			synchronized (pluginManager.botServiceFactories) {
				for (BotService.Factory factory : pluginManager.botServiceFactories) {
					BotService service = factory.createService(this);
					services.add(service);
					pluginManager.botServices.add(service);
				}
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BotService> T getService(Class<T> clazz) {
		synchronized (services) {
			for (BotService service : services) {
				if (clazz.isInstance(service))
					return (T)service;
			}
		}
		return null;
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