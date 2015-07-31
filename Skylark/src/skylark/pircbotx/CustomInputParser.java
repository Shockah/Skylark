package skylark.pircbotx;

import java.io.IOException;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import com.google.common.collect.ImmutableMap;
import skylark.pircbotx.event.AccountNotifyEvent;
import skylark.pircbotx.event.ExtendedJoinEvent;

public class CustomInputParser extends InputParser {
	protected Boolean
		availableAccountNotify = null,
		availableExtendedJoin = null;
	
	public CustomInputParser(PircBotX bot) {
		super(bot);
	}
	
	public void processCommand(String target, UserHostmask sourceh, String command, String line, List<String> parsedLine, ImmutableMap<String, String> map) throws IOException {
		Channel channel = (target.length() != 0 && configuration.getChannelPrefixes().indexOf(target.charAt(0)) >= 0 && bot.getUserChannelDao().containsChannel(target)) ? bot.getUserChannelDao().getChannel(target) : null;
		
		if (command.equals("ACCOUNT")) {
			if (availableAccountNotify == null)
				availableAccountNotify = bot.getEnabledCapabilities().contains("account-notify");
			if (availableAccountNotify) {
				String account = parsedLine.get(0);
				if (account.equals("0") || account.equals("*"))
					account = null;
				User source = bot.getUserChannelDao().getUser(sourceh);
				configuration.getListenerManager().onEvent(new AccountNotifyEvent(bot, channel, source, account));
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
					configuration.getListenerManager().onEvent(new ExtendedJoinEvent(bot, channel, source, account));
				}
			}
		}
		
		super.processCommand(target, sourceh, command, line, parsedLine, map);
	}
}