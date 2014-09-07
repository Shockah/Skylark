package shocky3;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import pl.shockah.Util;

public class BotManager {
	public final Shocky botApp;
	public final ServerManager manager;
	public final String name, host;
	public String botName = "Shocky";
	public int channelsPerConn = 10, messageDelay = 500;
	public final List<PircBotX> bots = Collections.synchronizedList(new LinkedList<PircBotX>());
	
	public BotManager(ServerManager manager, String name, String host) {
		this.botApp = manager.botApp;
		this.manager = manager;
		this.name = name;
		this.host = host;
	}
	
	public PircBotX joinChannel(String cname) {
		for (PircBotX bot : bots) {
			if (bot.getUserBot().getChannels().size() < channelsPerConn) {
				bot.sendIRC().joinChannel(cname);
				return bot;
			}
		}
		
		connectNewBot();
		return joinChannel(cname);
	}
	
	public boolean inAnyChannels() {
		for (PircBotX bot : bots) {
			if (bot.isConnected()) {
				if (!bot.getUserBot().getChannels().isEmpty()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public PircBotX connectNewBot() {
		final BotStarterThread botStarter = new BotStarterThread(this);
		botStarter.start();
		
		while (true) {
			if (botStarter.drop) break;
			Util.sleep(50);
		}
		
		try {
			if (botStarter.bot != null) {
				bots.add(botStarter.bot);
				for (Plugin plugin : botApp.pluginManager.plugins()) {
					plugin.onBotStarted(this, botStarter.bot);
				}
			}
		} catch (Exception e) {e.printStackTrace();}
		
		return botStarter.bot;
	}
}