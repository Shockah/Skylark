package me.shockah.skylark.plugin;

import me.shockah.skylark.Bot;

public interface BotService {
	public Instance createService(Bot bot);
	
	public static class Instance {
		public final Bot bot;
		
		public Instance(Bot bot) {
			this.bot = bot;
		}
	}
}