package me.shockah.skylark.ident;

import org.pircbotx.User;

public abstract class IdentMethod {
	public final IdentService service;
	public final String name;
	public final String prefix;
	
	public IdentMethod(IdentService service, String name, String prefix) {
		this.service = service;
		this.name = name;
		this.prefix = prefix;
	}
	
	public abstract boolean isAvailable();
	
	public abstract String getForUser(User user);
}