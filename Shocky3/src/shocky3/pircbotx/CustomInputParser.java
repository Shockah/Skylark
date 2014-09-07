package shocky3.pircbotx;

import java.io.IOException;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.User;

public class CustomInputParser extends InputParser {
	protected Boolean
		availableAccountNotify = null,
		availableExtendedJoin = null;
	
	public CustomInputParser(PircBotX bot) {
		super(bot);
	}
	
	public void processCommand(String target, String sourceNick, String sourceLogin, String sourceHostname, String command, String line, List<String> parsedLine) throws IOException {
		User source = bot.getUserChannelDao().getUser(sourceNick);
		Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().channelExists(target)) ? bot.getUserChannelDao().getChannel(target) : null;
		
		if (command.equals("ACCOUNT")) {
			if (availableAccountNotify == null) {
				availableAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
			}
			if (availableAccountNotify) {
				String account = parsedLine.get(0);
				if (account.equals("0") || account.equals("*")) {
					account = null;
				}
				configuration.getListenerManager().dispatchEvent(new AccountNotifyEvent<PircBotX>(bot, channel, source, account));
				return;
			}
		}
		
		super.processCommand(target, sourceNick, sourceLogin, sourceHostname, command, line, parsedLine);
		
		if (command.equals("JOIN")) {
			if (!sourceNick.equalsIgnoreCase(bot.getNick())) {
				if (availableExtendedJoin == null) {
					availableExtendedJoin = bot.getEnabledCapabilities().contains("extended-join");
				}
				if (availableExtendedJoin) {
					String account = parsedLine.get(1);
					if (account.equals("0") || account.equals("*")) {
						account = null;
					}
					configuration.getListenerManager().dispatchEvent(new ExtendedJoinEvent<PircBotX>(bot, channel, source, account));
				}
			}
		}
	}
}