package skylark.old.pircbotx.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutMessageEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {
	protected final Channel channel;
	protected final String message;
	
	public OutMessageEvent(PircBotX bot, Channel channel, String message) {
		super(bot);
		this.channel = channel;
		this.message = message;
	}

	public User getUser() {
		return bot.getUserBot();
	}
	public UserHostmask getUserHostmask() {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, getUser().getHostmask());
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
	public void respondPrivateMessage(String response) {
		throw new UnsupportedOperationException();
	}
	public void respondWith(String response) {
		throw new UnsupportedOperationException();
	}
}