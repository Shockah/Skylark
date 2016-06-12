package io.shockah.skylark.event;

import org.pircbotx.Channel;
import org.pircbotx.User;
import org.pircbotx.UserHostmask;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class GenericUserMessageEvent extends Event implements GenericMessageEvent, GenericChannelUserEvent {
	protected final Event event;
	protected final GenericMessageEvent eMessage;
	protected final GenericChannelUserEvent eChannelUser;
	
	public GenericUserMessageEvent(MessageEvent e) {
		super(e.getBot());
		event = e;
		eMessage = e;
		eChannelUser = e;
	}
	
	public GenericUserMessageEvent(ActionEvent e) {
		super(e.getBot());
		event = e;
		eMessage = e;
		eChannelUser = e;
	}
	
	public GenericUserMessageEvent(PrivateMessageEvent e) {
		super(e.getBot());
		event = e;
		eMessage = e;
		eChannelUser = null;
	}
	
	public GenericUserMessageEvent(NoticeEvent e) {
		super(e.getBot());
		event = e;
		eMessage = e;
		eChannelUser = null;
	}
	
	public Event getEvent() {
		return event;
	}

	public Channel getChannel() {
		return eChannelUser != null ? eChannelUser.getChannel() : null;
	}
	
	public UserHostmask getUserHostmask() {
		return eMessage != null ? eMessage.getUserHostmask() : (eChannelUser != null ? eChannelUser.getUserHostmask() : null);
	}
	
	public User getUser() {
		return eMessage != null ? eMessage.getUser() : (eChannelUser != null ? eChannelUser.getUser() : null);
	}
	
	public String getMessage() {
		return eMessage != null ? eMessage.getMessage() : null;
	}
	
	public void respond(String response) {
		if (eMessage != null)
			eMessage.respond(response);
		else if (eChannelUser != null)
			eChannelUser.respond(response);
	}
	
	public void respondPrivateMessage(String response) {
		if (eMessage != null)
			eMessage.respondPrivateMessage(response);
		else if (eChannelUser != null)
			throw new UnsupportedOperationException();
	}
	
	public void respondWith(String response) {
		if (eMessage != null)
			eMessage.respondWith(response);
		else if (eChannelUser != null)
			throw new UnsupportedOperationException();
	}
}