package shocky3.pircbotx;

import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PrivateMessageEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import org.pircbotx.hooks.types.GenericMessageEvent;

public class NullableChannelUserEvent<T extends PircBotX> extends Event<T> implements GenericMessageEvent<T>, GenericChannelUserEvent<T> {
	protected final GenericMessageEvent<T> eMessage;
	protected final GenericChannelUserEvent<T> eChannelUser;
	
	public NullableChannelUserEvent(MessageEvent<T> e) {
		super(e.getBot());
		eMessage = e;
		eChannelUser = e;
	}
	public NullableChannelUserEvent(ActionEvent<T> e) {
		super(e.getBot());
		eMessage = e;
		eChannelUser = e;
	}
	public NullableChannelUserEvent(PrivateMessageEvent<T> e) {
		super(e.getBot());
		eMessage = e;
		eChannelUser = null;
	}
	public NullableChannelUserEvent(NoticeEvent<T> e) {
		super(e.getBot());
		eMessage = e;
		eChannelUser = null;
	}

	public Channel getChannel() {return eChannelUser != null ? eChannelUser.getChannel() : null;}
	public User getUser() {return eMessage != null ? eMessage.getUser() : (eChannelUser != null ? eChannelUser.getUser() : null);}
	public String getMessage() {return eMessage != null ? eMessage.getMessage() : null;}
	
	public void respond(String response) {
		if (eMessage != null) {
			eMessage.respond(response);
		} else if (eChannelUser != null) {
			eChannelUser.respond(response);
		}
	}
}