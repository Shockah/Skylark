package shocky3.pircbotx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import shocky3.BotManager;
import shocky3.Shocky;
import shocky3.pircbotx.event.OutActionEvent;
import shocky3.pircbotx.event.OutMessageEvent;
import shocky3.pircbotx.event.OutNoticeEvent;
import shocky3.pircbotx.event.OutPrivateMessageEvent;

public class Bot extends PircBotX {
	public final Shocky botApp;
	public final BotManager manager;
	public final List<User> blockedDCC = Collections.synchronizedList(new LinkedList<User>());
	
	public Bot(Configuration<? extends Bot> configuration, BotManager manager) {
		super(configuration);
		botApp = manager.botApp;
		this.manager = manager;
	}
	
	protected void sendRawLineToServer(String line) {
		String[] spl = line.split("\\s");
		spl[0] = spl[0].toUpperCase();
		
		if (spl[0].equals("PRIVMSG")) {
			String target = spl[1];
			String message = StringUtils.stripEnd(line.substring(spl[0].length() + spl[1].length() + 3), null);
			
			Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && getUserChannelDao().channelExists(target)) ? getUserChannelDao().getChannel(target) : null;
			if (channel == null) {
				User recipient = getUserChannelDao().getUser(target);
				configuration.getListenerManager().dispatchEvent(new OutPrivateMessageEvent<PircBotX>(this, recipient, message));
			} else {
				if (message.startsWith("\u0001") && message.endsWith("\u0001")) {
					message = message.substring(1, message.length() - 1);
					spl = message.split("\\s");
					spl[0] = spl[0].toUpperCase();
					message = message.substring(spl[0].length() + 1);
					
					if (spl[0].equals("ACTION")) {
						configuration.getListenerManager().dispatchEvent(new OutActionEvent<PircBotX>(this, channel, message));
					}
				} else {
					configuration.getListenerManager().dispatchEvent(new OutMessageEvent<PircBotX>(this, channel, message));
				}
			}
		} else if (spl[0].equals("NOTICE")) {
			String target = spl[1];
			String message = StringUtils.stripEnd(line.substring(spl[0].length() + spl[1].length() + 3), null);
			
			Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && getUserChannelDao().channelExists(target)) ? getUserChannelDao().getChannel(target) : null;
			if (channel == null) {
				User recipient = getUserChannelDao().getUser(target);
				configuration.getListenerManager().dispatchEvent(new OutNoticeEvent<PircBotX>(this, null, recipient, message));
			} else {
				configuration.getListenerManager().dispatchEvent(new OutNoticeEvent<PircBotX>(this, channel, null, message));
			}
		}
		
		super.sendRawLineToServer(line);
	}
}