package me.shockah.skylark.event;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutPrivateMessageEvent extends Event implements GenericMessageEvent {
	protected final User recipient;
	protected final String message;
	
	public OutPrivateMessageEvent(PircBotX bot, User recipient, String message) {
		super(bot);
		this.recipient = recipient;
		this.message = message;
	}

	public User getUser() {
		return bot.getUserBot();
	}
	public UserHostmask getUserHostmask() {
		return bot.getConfiguration().getBotFactory().createUserHostmask(bot, getUser().getHostmask());
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