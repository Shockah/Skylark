package io.shockah.skylark.groovy;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandCall;

public class UserGroovySandboxImpl extends GroovySandboxImpl {
	public UserGroovySandboxImpl() {
		super();
		
		addBlacklistedMethods(CommandCall.class,
				"respond"
		);
		
		addBlacklistedFields(Bot.class,
				"manager"
		);
		
		addWhitelistedMethods(PircBotX.class,
				"getNick", "getServerHostname", "getServerPort", "getUserBot", "getUserChannelDao"
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
		
		addWhitelistedPackages(
				"io.shockah.skylark.commands"
		);
	}
}