package me.shockah.skylark;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;

public class Bot extends PircBotX {
	public final BotManager manager;
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		this.manager = manager;
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