package me.shockah.skylark;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import me.shockah.skylark.plugin.BotService;

public class Bot extends PircBotX {
	public final BotManager manager;
	
	public final List<BotService.Instance> services = Collections.synchronizedList(new ArrayList<>());
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		this.manager = manager;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BotService.Instance> T getService(Class<T> clazz) {
		synchronized (services) {
			for (BotService.Instance service : services) {
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