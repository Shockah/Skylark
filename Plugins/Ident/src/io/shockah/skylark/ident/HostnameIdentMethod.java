package io.shockah.skylark.ident;

import org.pircbotx.User;

public class HostnameIdentMethod extends IdentMethod {
	public static final String METHOD_NAME = "Hostname";
	public static final String METHOD_PREFIX = "h";
	
	public HostnameIdentMethod(IdentService service, IdentMethodFactory factory) {
		super(service, factory, METHOD_NAME, METHOD_PREFIX);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getForUser(User user) {
		return user.getHostname();
	}
	
	public static class Factory extends IdentMethodFactory {
		public Factory() {
			super(METHOD_NAME, METHOD_PREFIX);
		}

		@Override
		public IdentMethod create(IdentService service) {
			return new HostnameIdentMethod(service, this);
		}
	}
}