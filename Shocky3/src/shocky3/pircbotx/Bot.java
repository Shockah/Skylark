package shocky3.pircbotx;

import org.apache.commons.lang3.StringUtils;
import org.pircbotx.Channel;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class Bot extends PircBotX {
	public Bot(Configuration<? extends Bot> configuration) {
		super(configuration);
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
					configuration.getListenerManager().dispatchEvent(new OutActionEvent<PircBotX>(this, channel, message));
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