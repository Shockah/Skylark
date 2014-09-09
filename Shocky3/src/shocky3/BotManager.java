package shocky3;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.PircBotX;
import pl.shockah.Util;
import shocky3.pircbotx.Bot;

public class BotManager {
	public static final int
		BASE_CHANNELLIMIT = 10, BASE_MESSAGEDELAY = 500;
	
	public final Shocky botApp;
	public final ServerManager manager;
	public final String name, host;
	public String botName = "Shocky";
	public int channelsPerConn = -1, messageDelay = BASE_MESSAGEDELAY;
	public final List<Bot> bots = Collections.synchronizedList(new LinkedList<Bot>());
	
	public BotManager(ServerManager manager, String name, String host) {
		this.botApp = manager.botApp;
		this.manager = manager;
		this.name = name;
		this.host = host;
	}
	
	public Bot joinChannel(String cname) {
		if (channelsPerConn == 0) {
			return null;
		}
		if (channelsPerConn < 0) {
			if (bots.isEmpty()) {
				connectNewBot();
			}
			if (!bots.isEmpty()) {
				try {
					channelsPerConn = Integer.parseInt(bots.get(0).getServerInfo().getChanlimit());
					if (channelsPerConn < 0) {
						channelsPerConn = BASE_CHANNELLIMIT;
					}
				} catch (Exception e) {
					channelsPerConn = BASE_CHANNELLIMIT;
				}
			}
		}
		
		for (Bot bot : bots) {
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
	
	public Bot connectNewBot() {
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