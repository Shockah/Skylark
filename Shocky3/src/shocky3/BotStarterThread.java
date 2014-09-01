package shocky3;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;

public class BotStarterThread extends Thread {
	public final Shocky botApp;
	public final BotManager manager;
	
	public PircBotX bot = null;
	public boolean drop = false;
	
	public BotStarterThread(BotManager manager) {
		this.botApp = manager.botApp;
		this.manager = manager;
	}
	
	public void run() {
		try {
			final BotStarterThread THIS = this;
			Configuration.Builder<PircBotX> cfgb = new Configuration.Builder<>()
				.setName(manager.botName)
				.setAutoNickChange(true)
				.setServerHostname(manager.host)
				.setMessageDelay(manager.messageDelay)
				.addListener(new Listener<PircBotX>(){
					public void onEvent(Event<PircBotX> e) throws Exception {
						if (e instanceof ConnectEvent) {
							THIS.bot = e.getBot();
							drop = true;
						}
					}
				}
			);
			for (Plugin plugin : botApp.pluginManager.plugins()) {
				if (plugin instanceof ListenerPlugin) {
					cfgb.addListener(((ListenerPlugin)plugin).listener);
				}
			}
			
			PircBotX bot = new PircBotX(cfgb.buildConfiguration());
			bot.startBot();
		} catch (Exception e) {
			this.bot = null;
			drop = true;
			e.printStackTrace();
		}
	}
}