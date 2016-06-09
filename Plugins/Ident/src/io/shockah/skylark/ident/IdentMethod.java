package io.shockah.skylark.ident;

import org.pircbotx.User;

public abstract class IdentMethod {
	public final IdentService service;
	public final IdentMethodFactory factory;
	public final String name;
	public final String prefix;
	
	public IdentMethod(IdentService service, IdentMethodFactory factory, String name, String prefix) {
		this.service = service;
		this.factory = factory;
		this.name = name;
		this.prefix = prefix;
	}
	
	public abstract boolean isAvailable();
	
	public abstract String getForUser(User user);
}