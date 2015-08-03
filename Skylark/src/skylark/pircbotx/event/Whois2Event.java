package skylark.pircbotx.event;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.WhoisEvent;
import com.google.common.collect.ImmutableList;

public class Whois2Event extends Event {
	protected final WhoisEvent event;
	protected final String[] messages;
	
	public Whois2Event(WhoisEvent event, String[] messages) {
		super(event.getBot());
		this.event = event;
		this.messages = messages;
	}
	
	public String getNick() {
		return event.getNick();
	}
	
	public String getLogin() {
		return event.getLogin();
	}
	
	public String getHostname() {
		return event.getHostname();
	}
	
	public String getRealname() {
		return event.getRealname();
	}
	
	public ImmutableList<String> getChannels() {
		return event.getChannels();
	}
	
	public String getServer() {
		return event.getServer();
	}
	
	public String getServerInfo() {
		return event.getServerInfo();
	}
	
	public long getIdleSeconds() {
		return event.getIdleSeconds();
	}
	
	public long getSignOnTime() {
		return event.getSignOnTime();
	}
	
	public String getRegisteredAs() {
		return event.getRegisteredAs();
	}
	
	public boolean isExists() {
		return event.isExists();
	}
	
	public String getAwayMessage() {
		return event.getAwayMessage();
	}
	
	public String[] getMessages() {
		return messages;
	}
	
	public boolean isRegistered() {
		return event.isRegistered();
	}
	
	public void respond(String response) {
		event.respond(response);
	}
}