package io.shockah.skylark;

import io.shockah.skylark.event.AccountNotifyEvent;
import io.shockah.skylark.event.ExtendedJoinEvent;
import io.shockah.skylark.event.Whois2Event;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.pircbotx.Channel;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.ReplyConstants;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.events.WhoisEvent;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class SkylarkInputParser extends InputParser {
	public static final String OPERATOR_STATUS_PREFIX = "is a ";
	public static final int RPL_WHOISMESSAGE = 313;
	
	protected Boolean availableAccountNotify = null;
	protected Boolean availableExtendedJoin = null;
	protected Map<String, String> whoisOperatorStatusBuilder = Collections.synchronizedMap(new HashMap<>());
	
	public SkylarkInputParser(PircBotX bot) {
		super(bot);
	}
	
	@Override
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
	
	@Override
	public void processServerResponse(int code, String rawResponse, List<String> parsedResponseOrig) {
		ImmutableList<String> parsedResponse = ImmutableList.copyOf(parsedResponseOrig);
		
		if (code == RPL_WHOISMESSAGE) {
			String whoisNick = parsedResponse.get(1);
			String operatorStatus = parsedResponse.get(2);
			if (operatorStatus.startsWith(OPERATOR_STATUS_PREFIX)) {
				operatorStatus = operatorStatus.substring(OPERATOR_STATUS_PREFIX.length());
				whoisOperatorStatusBuilder.put(whoisNick, operatorStatus);
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
			String operatorStatus = whoisOperatorStatusBuilder.get(whoisNick);
			WhoisEvent event = builder.generateEvent(bot);
			configuration.getListenerManager().onEvent(event);
			configuration.getListenerManager().onEvent(new Whois2Event(event, operatorStatus));
			whoisBuilder.remove(whoisNick);
			whoisOperatorStatusBuilder.remove(whoisNick);
		} else {
			super.processServerResponse(code, rawResponse, parsedResponseOrig);
		}
	}
}