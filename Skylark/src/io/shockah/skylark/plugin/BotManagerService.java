package io.shockah.skylark.plugin;

import io.shockah.skylark.BotManager;

public abstract class BotManagerService {
	public final BotManager manager;
	
	public BotManagerService(BotManager manager) {
		this.manager = manager;
	}
	
	public static interface Factory {
		public BotManagerService createService(BotManager manager);
	}
}