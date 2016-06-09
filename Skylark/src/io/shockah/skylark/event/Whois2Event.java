package io.shockah.skylark.event;

import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.WhoisEvent;
import com.google.common.collect.ImmutableList;

public class Whois2Event extends Event {
	protected final WhoisEvent event;
	protected final String operatorStatus;
	
	public Whois2Event(WhoisEvent event, String operatorStatus) {
		super(event.getBot());
		this.event = event;
		this.operatorStatus = operatorStatus;
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
	
	public String getOperatorStatus() {
		return operatorStatus;
	}
	
	public boolean isRegistered() {
		return event.isRegistered();
	}
	
	public void respond(String response) {
		event.respond(response);
	}
}