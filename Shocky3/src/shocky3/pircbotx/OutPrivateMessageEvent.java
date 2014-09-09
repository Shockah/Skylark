package shocky3.pircbotx;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class OutPrivateMessageEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T> {
	protected final User recipient;
	protected final String message;
	
	public OutPrivateMessageEvent(T bot, User recipient, String message) {
		super(bot);
		this.recipient = recipient;
		this.message = message;
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