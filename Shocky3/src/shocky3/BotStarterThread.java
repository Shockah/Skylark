package shocky3;

import java.nio.charset.Charset;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.OutputEvent;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.CustomInputParser;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;

public class BotStarterThread extends Thread {
	public final Shocky botApp;
	public final BotManager manager;
	
	public Bot bot = null;
	public boolean drop = false;
	
	public BotStarterThread(BotManager manager) {
		this.botApp = manager.botApp;
		this.manager = manager;
	}
	
	public void run() {
		try {
			Configuration.Builder cfgb = new Configuration.Builder()
				.setBotFactory(new BotFactory(){
					public InputParser createInputParser(PircBotX bot) {
						return new CustomInputParser(bot);
					}
				})
				.setEncoding(Charset.forName("UTF-8"))
				.setName(manager.botName)
				.setAutoNickChange(true)
				.setMessageDelay(manager.messageDelay)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("extended-join", true))
				.addCapHandler(new EnableCapHandler("account-notify", true))
				.setAutoReconnect(true)
				.addServer(manager.host)
				.addListener(new ListenerAdapter(){
					public void onEvent(Event e) throws Exception {
						if (e instanceof ConnectEvent) {
							BotStarterThread.this.bot = e.getBot();
							drop = true;
						}
						super.onEvent(e);
					}
					public void onOutput(OutputEvent e) {
						PircBotX bot = e.getBot();
						Configuration configuration = bot.getConfiguration();
						String line = e.getRawLine();
						String[] spl = line.split("\\s");
						spl[0] = spl[0].toUpperCase();
						
						if (spl[0].equals("PRIVMSG")) {
							String target = spl[1];
							String message = StringUtils.stripEnd(line.substring(spl[0].length() + spl[1].length() + 3), null);
							
							Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().containsChannel(target)) ? bot.getUserChannelDao().getChannel(target) : null;
							if (channel == null) {
								User recipient = bot.getUserChannelDao().getUser(target);
								configuration.getListenerManager().dispatchEvent(new OutPrivateMessageEvent(bot, recipient, message));
							} else {
								if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
									message = message.substring(1, message.length() - 1);
									spl = message.split("\\s");
									spl[0] = spl[0].toUpperCase();
									message = message.substring(spl[0].length() + 1);
									
									if (spl[0].equals("ACTION")) {
										configuration.getListenerManager().dispatchEvent(new OutActionEvent(bot, channel, message));
									}
								} else {
									configuration.getListenerManager().dispatchEvent(new OutMessageEvent(bot, channel, message));
								}
							}
						} else if (spl[0].equals("NOTICE")) {
							String target = spl[1];
							String message = StringUtils.stripEnd(line.substring(spl[0].length() + spl[1].length() + 3), null);
							
							Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().containsChannel(target)) ? bot.getUserChannelDao().getChannel(target) : null;
							if (channel == null) {
								User recipient = bot.getUserChannelDao().getUser(target);
								configuration.getListenerManager().dispatchEvent(new OutNoticeEvent(bot, null, recipient, message));
							} else {
								configuration.getListenerManager().dispatchEvent(new OutNoticeEvent(bot, channel, null, message));
							}
						}
					}
				}
			);
			synchronized (botApp.pluginManager.plugins) {for (Plugin plugin : botApp.pluginManager.plugins) {
				if (plugin instanceof ListenerPlugin) {
					cfgb.addListener(((ListenerPlugin)plugin).listener);
				}
			}}
			
			bot = new Bot(cfgb.buildConfiguration(), manager);
			bot.startBot();
		} catch (Exception e) {
			this.bot = null;
			drop = true;
			e.printStackTrace();
		}
	}
}