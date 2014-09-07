package shocky3.pircbotx;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;

public class ExtendedJoinEvent<T extends PircBotX> extends Event<T> implements GenericChannelUserEvent<T> {
	protected final Channel channel;
	protected final User user;
	protected final String account;
	
	public ExtendedJoinEvent(T bot, Channel channel, User user, String account) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.account = account;
	}
	
	public User getUser() {
		return user;
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