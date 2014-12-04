package shocky3.pircbotx.event;

import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class ServerNoticeEvent extends Event implements GenericMessageEvent {
	protected final User user;
	protected final UserHostmask userHostmask;
	protected final String message;
	
	public ServerNoticeEvent(PircBotX bot, UserHostmask userHostmask, User user, String message) {
		super(bot);
		this.user = user;
		this.userHostmask = userHostmask;
		this.message = message;
	}

	public User getUser() {
		return user;
	}
	public UserHostmask getUserHostmask() {
		return userHostmask;
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
}