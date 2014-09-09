package shocky3.pircbotx.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutMessageEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T> {
	protected final Channel channel;
	protected final String message;
	
	public OutMessageEvent(T bot, Channel channel, String message) {
		super(bot);
		this.channel = channel;
		this.message = message;
	}

	public User getUser() {
		return bot.getUserBot();
	}

	public Channel getChannel() {
		return channel;
	}

	public String getMessage() {
		return message;
	}

	public void respond(String response) {
		throw new UnsupportedOperationException();
	}
}