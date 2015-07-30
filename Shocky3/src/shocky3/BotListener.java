package shocky3;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import org.pircbotx.hooks.events.OutputEvent;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;

public class BotListener extends ListenerAdapter {
	public final BotStarterThread botStarterThread;
	
	public BotListener(BotStarterThread botStarterThread) {
		super();
		this.botStarterThread = botStarterThread;
	}
	
	public void onEvent(Event e) throws Exception {
		if (e instanceof ConnectEvent) {
			botStarterThread.bot = e.getBot();
			botStarterThread.drop = true;
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
				configuration.getListenerManager().onEvent(new OutPrivateMessageEvent(bot, recipient, message));
			} else {
				if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
					message = message.substring(1, message.length() - 1);
					spl = message.split("\\s");
					spl[0] = spl[0].toUpperCase();
					message = message.substring(spl[0].length() + 1);
					
					if (spl[0].equals("ACTION"))
						configuration.getListenerManager().onEvent(new OutActionEvent(bot, channel, message));
				} else {
					configuration.getListenerManager().onEvent(new OutMessageEvent(bot, channel, message));
				}
			}
		} else if (spl[0].equals("NOTICE")) {
			String target = spl[1];
			String message = StringUtils.stripEnd(line.substring(spl[0].length() + spl[1].length() + 3), null);
			
			Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().containsChannel(target)) ? bot.getUserChannelDao().getChannel(target) : null;
			if (channel == null) {
				User recipient = bot.getUserChannelDao().getUser(target);
				configuration.getListenerManager().onEvent(new OutNoticeEvent(bot, null, recipient, message));
			} else {
				configuration.getListenerManager().onEvent(new OutNoticeEvent(bot, channel, null, message));
			}
		}
	}
}