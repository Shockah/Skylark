package me.shockah.skylark.plugin;

import me.shockah.skylark.Bot;

public class BotService {
	public final Bot bot;
	
	public BotService(Bot bot) {
		this.bot = bot;
	}
	
	public static interface Factory {
		public BotService createService(Bot bot);
	}
}