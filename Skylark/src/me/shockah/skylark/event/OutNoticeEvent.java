package me.shockah.skylark.event;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutNoticeEvent extends Event implements GenericChannelUserEvent, GenericMessageEvent {
	protected final Channel channel;
	protected final User recipient;
	protected final String message;
	
	public OutNoticeEvent(PircBotX bot, Channel channel, User recipient, String message) {
		super(bot);
		this.channel = channel;
		this.recipient = recipient;
		this.message = message;
	}
	
	public Channel getChannel() {
		return channel;
	}
	public UserHostmask getUserHostmask() {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, getUser().getHostmask());
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
	public void respondPrivateMessage(String response) {
		throw new UnsupportedOperationException();
	}
	public void respondWith(String response) {
		throw new UnsupportedOperationException();
	}
}