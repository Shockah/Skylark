package shocky3.pircbotx;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutNoticeEvent<T extends PircBotX> extends Event<T> implements GenericChannelUserEvent<T>, GenericMessageEvent<T> {
	protected final Channel channel;
	protected final User recipient;
	protected final String message;
	
	public OutNoticeEvent(T bot, Channel channel, User recipient, String message) {
		super(bot);
		this.channel = channel;
		this.recipient = recipient;
		this.message = message;
	}
	
	public Channel getChannel() {
		return channel;
	}

	public User getUser() {
		return bot.getUserBot();
	}

	public User getRecipient() {
		return recipient;
	}

	public String getMessage() {
		return message;
	}

	public void respond(String response) {
		throw new UnsupportedOperationException();
	}
}