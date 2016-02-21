package me.shockah.skylark.plugin;

import me.shockah.skylark.BotManager;

public interface BotManagerService {
	public Instance createService(BotManager manager);
	
	public static class Instance {
		public final BotManager manager;
		
		public Instance(BotManager manager) {
			this.manager = manager;
		}
	}
}