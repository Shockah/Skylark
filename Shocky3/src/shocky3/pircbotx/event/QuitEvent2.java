package shocky3.pircbotx.event;

import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.PircBotX;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.types.GenericUserEvent;
import org.pircbotx.snapshot.UserChannelDaoSnapshot;
import org.pircbotx.snapshot.UserSnapshot;

public class QuitEvent2<T extends PircBotX> extends Event<T> implements GenericUserEvent<T> {
	protected final UserChannelDaoSnapshot daoSnapshot;
	protected final UserSnapshot user;
	protected final String reason;
	
	protected final List<Channel> channels;
	
	public QuitEvent2(T bot, UserChannelDaoSnapshot daoSnapshot, UserSnapshot user, String reason, List<Channel> channels) {
		super(bot);
		this.daoSnapshot = daoSnapshot;
		this.user = user;
		this.reason = reason;
		this.channels = channels;
	}
	
	public UserSnapshot getUser() {
		return user;
	}
	
	public List<Channel> getChannels() {
		return channels;
	}
	
	public void respond(String response) {
		throw new UnsupportedOperationException("Attempting to respond to a user that quit");
	}
}