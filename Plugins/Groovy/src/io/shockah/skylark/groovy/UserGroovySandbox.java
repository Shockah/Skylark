package io.shockah.skylark.groovy;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandCall;

public class UserGroovySandbox extends GroovySandbox {
	public UserGroovySandbox() {
		super();
		addBlacklistedMethods(CommandCall.class, "respond");
		
		addWhitelistedMethods(PircBotX.class,
				"getNick", "getServerHostname", "getServerPort", "getUserBot", "getUserChannelDao"
		);
		
		addBlacklistedMethods(Bot.class,
				"getBotManager"
		);
		
		addWhitelistedMethods(User.class,
				"getAwayMessage", "getBot", "getHostmask", "getHostname", "getLogin", "getNick", "getRealName", "getServer",
				"getChannels", "getChannelsHalfOpIn", "getChannelsOpIn", "getChannelsOwnerIn", "getChannelsSuperOpIn", "getChannelsVoiceIn",
				"isAway", "isIrcop"
		);
		
		addWhitelistedMethods(Channel.class,
				"getBot", "getChannelLimit", "getMode", "getName", "getTopic", "getTopicSetter", "getTopicTimestamp",
				"getHalfOps", "getNormalUsers", "getOps", "getOwners", "getSuperOps", "getUsers", "getUsersNicks", "getVoices",
				"hasVoice", "isHalfOp", "isOp", "isOwner", "isSuperOp",
				"hasTopicProtection", "isChannelPrivate", "isInviteOnly", "isModerated", "isNoExternalMessages", "isSecret"
		);
		
		addWhitelistedMethods(UserHostmask.class,
				"getHostmask", "getHostname", "getLogin", "getNick"
		);
	}
}