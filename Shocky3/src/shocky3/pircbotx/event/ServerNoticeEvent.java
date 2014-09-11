package shocky3.pircbotx.event;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ServerNoticeEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T> {
	protected final User user;
	protected final String message;
	
	public ServerNoticeEvent(T bot, User user, String message) {
		super(bot);
		this.user = user;
		this.message = message;
	}

	public User getUser() {
		return user;
	}

	public String getMessage() {
		return message;
	}
	
	public void respond(String response) {
		throw new UnsupportedOperationException();
	}
}