package skylark.pircbotx.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;

public class AccountNotifyEvent extends Event implements GenericChannelUserEvent {
	protected final Channel channel;
	protected final User user;
	protected final String account;
	
	public AccountNotifyEvent(PircBotX bot, Channel channel, User user, String account) {
		super(bot);
		this.channel = channel;
		this.user = user;
		this.account = account;
	}
	
	public User getUser() {
		return user;
	}
	public UserHostmask getUserHostmask() {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, user.getHostmask());
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