package shocky3.pircbotx.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;

public class AccountNotifyEvent extends Event implements GenericChannelUserEvent {
	protected final Channel channel;
	protected final User user;
	protected final UserHostmask userHostmask;
	protected final String account;
	
	public AccountNotifyEvent(PircBotX bot, Channel channel, UserHostmask userHostmask, User user, String account) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.userHostmask = userHostmask;
		this.account = account;
	}
	
	public User getUser() {
		return user;
	}
	public UserHostmask getUserHostmask() {
		return userHostmask;
	}
	public Channel getChannel() {
		return channel;
	}
	public String getAccount() {
		return account;
	}
	
	public void respond(String response) {
		getChannel().send().message(getUser(), response);
	}
}