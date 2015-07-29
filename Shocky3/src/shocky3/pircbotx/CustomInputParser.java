package shocky3.pircbotx;

import java.io.IOException;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import shocky3.pircbotx.event.AccountNotifyEvent;
import shocky3.pircbotx.event.ExtendedJoinEvent;

public class CustomInputParser extends InputParser {
	protected Boolean
		availableAccountNotify = null,
		availableExtendedJoin = null;
	
	public CustomInputParser(PircBotX bot) {
		super(bot);
	}
	
	public void processCommand(String target, UserHostmask sourceh, String command, String line, List<String> parsedLine) throws IOException {
		Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().containsChannel(target)) ? bot.getUserChannelDao().getChannel(target) : null;
		
		if (command.equals("ACCOUNT")) {
			if (availableAccountNotify == null)
				availableAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
			if (availableAccountNotify) {
				String account = parsedLine.get(0);
				if (account.equals("0") || account.equals("*"))
					account = null;
				User source = bot.getUserChannelDao().getUser(sourceh);
				configuration.getListenerManager().dispatchEvent(new AccountNotifyEvent(bot, channel, new UserHostmask(source), source, account));
				return;
			}
		} else if (command.equals("JOIN")) {
			User source = bot.getUserChannelDao().getUser(sourceh);
			String sourceNick = source.getNick();
			if (!sourceNick.equalsIgnoreCase(bot.getNick())) {
				if (availableExtendedJoin == null)
					availableExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
				if (availableExtendedJoin) {
					String account = parsedLine.get(1);
					if (account.equals("0") || account.equals("*"))
						account = null;
					configuration.getListenerManager().dispatchEvent(new ExtendedJoinEvent(bot, channel, new UserHostmask(source), source, account));
				}
			}
		}
		
		super.processCommand(target, sourceh, command, line, parsedLine);
	}
}