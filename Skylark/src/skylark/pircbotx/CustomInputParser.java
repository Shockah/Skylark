package skylark.pircbotx;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import org.pircbotx.Channel;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.WhoisEvent;
import skylark.pircbotx.event.AccountNotifyEvent;
import skylark.pircbotx.event.ExtendedJoinEvent;
import skylark.pircbotx.event.Whois2Event;
import skylark.util.Synced;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class CustomInputParser extends InputParser {
	public static final int
		RPL_WHOISMESSAGE = 313;
	
	protected Boolean
		availableAccountNotify = null,
		availableExtendedJoin = null;
	protected
		Map<String, List<String>> whoisMessagesBuilder = Synced.map();
	
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
	
	public void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
		ImmutableList<String> parsedResponse = ImmutableList.copyOf(parsedResponseOrig);
		
		if (code == RPL_WHOISMESSAGE) {
			synchronized (whoisMessagesBuilder) {
				String whoisNick = parsedResponse.get(1);
				List<String> messages = whoisMessagesBuilder.get(whoisNick);
				if (messages == null) {
					messages = Synced.<String>list();
					whoisMessagesBuilder.put(whoisNick, messages);
				}
				messages.add(parsedResponse.get(2));
			}
		} else if (code == ReplyConstants.RPL_ENDOFWHOIS) {
			String whoisNick = parsedResponse.get(1);
			WhoisEvent.Builder builder;
			if (whoisBuilder.containsKey(whoisNick)) {
				builder = whoisBuilder.get(whoisNick);
				builder.exists(true);
			} else {
				builder = WhoisEvent.builder();
				builder.nick(whoisNick);
				builder.exists(false);
			}
			List<String> messages = whoisMessagesBuilder.get(whoisNick);
			String[] messagesArray = messages == null ? new String[0] : messages.toArray(new String[0]);
			WhoisEvent event = builder.generateEvent(bot);
			configuration.getListenerManager().onEvent(event);
			configuration.getListenerManager().onEvent(new Whois2Event(event, messagesArray));
			whoisBuilder.remove(whoisNick);
			whoisMessagesBuilder.remove(whoisNick);
		} else {
			super.processServerResponse(code, rawResponse, parsedResponseOrig);
		}
	}
}