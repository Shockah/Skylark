package me.shockah.skylark.ident;

import org.pircbotx.User;

public class HostnameIdentMethod extends IdentMethod {
	public HostnameIdentMethod(IdentService service) {
		super(service, "Hostname", "h");
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getForUser(User user) {
		return user.getHostname();
	}
}