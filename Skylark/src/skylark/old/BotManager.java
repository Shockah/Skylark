package skylark.old;

import java.util.List;
import java.util.Random;
import org.pircbotx.Channel;
import pl.shockah.Util;
import skylark.old.pircbotx.Bot;
import skylark.old.util.Synced;

public class BotManager {
	public static final int
		BASE_CHANNELLIMIT = 10,
		BASE_MESSAGEDELAY = 500;
	
	public final Skylark botApp;
	public final ServerManager manager;
	public final String name, host;
	public String botName = "Shocky";
	public int channelsPerConn = -1, messageDelay = BASE_MESSAGEDELAY;
	public final List<Bot> bots = Synced.list();
	
	public BotManager(ServerManager manager, String name, String host) {
		this.botApp = manager.botApp;
		this.manager = manager;
		this.name = name;
		this.host = host;
	}
	
	public Bot joinChannel(String channelName) {
		if (channelsPerConn == 0)
			return null;
		
		synchronized (bots) {
			Bot existing = botForChannel(channelName);
			if (existing != null)
				return existing;
			
			if (channelsPerConn < 0) {
				if (bots.isEmpty())
					connectNewBot();
				if (!bots.isEmpty()) {
					try {
						channelsPerConn = Integer.parseInt(bots.get(0).getServerInfo().getChanlimit());
						if (channelsPerConn < 0)
							channelsPerConn = BASE_CHANNELLIMIT;
					} catch (Exception e) {
						channelsPerConn = BASE_CHANNELLIMIT;
					}
				}
			}
			
			for (Bot bot : bots)
				if (bot.getUserBot().getChannels().size() < channelsPerConn) {
					bot.sendIRC().joinChannel(channelName);
					return bot;
				}
			connectNewBot();
		}
		return joinChannel(channelName);
	}
	
	public Bot partChannel(String channelName) {
		synchronized (bots) {
			Bot bot = botForChannel(channelName);
			if (bot != null)
				for (Channel channel : bot.getUserBot().getChannels())
					if (channel.getName().equalsIgnoreCase(channelName)) {
						channel.send().part();
						return bot;
					}
			return bot;
		}
	}
	
	public boolean inChannel(String channelName) {
		return botForChannel(channelName) != null;
	}
	
	public Bot botForChannel(String channelName) {
		synchronized (bots) {
			for (Bot bot : bots)
				if (bot.isConnected())
					for (Channel channel : bot.getUserBot().getChannels())
						if (channel.getName().equalsIgnoreCase(channelName))
							return bot;
			return null;
		}
	}
	
	public Bot anyBot() {
		//TODO: could sometime in future pick the bot having the least queued messages
		if (bots.isEmpty())
			return null;
		return bots.get(new Random().nextInt(bots.size()));
	}
	
	public boolean inAnyChannels() {
		synchronized (bots) {
			for (Bot bot : bots)
				if (bot.isConnected())
					if (!bot.getUserBot().getChannels().isEmpty())
						return true;
		}
		return false;
	}
	
	public Bot connectNewBot() {
		final BotStarterThread botStarter = new BotStarterThread(this);
		botStarter.start();
		
		while (true) {
			if (botStarter.drop)
				break;
			Util.sleep(50);
		}
		
		try {
			synchronized (bots) {
				if (botStarter.bot != null) {
					botStarter.bot.getConfiguration().getListenerManager().addListener(botStarter.bot.whoisManager);
					bots.add(botStarter.bot);
					synchronized (botApp.pluginManager.plugins) {
						for (Plugin plugin : botApp.pluginManager.plugins)
							plugin.onBotStarted(this, botStarter.bot);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return botStarter.bot;
	}
}