package io.shockah.skylark.ident;

import org.pircbotx.User;

public class NameIdentMethod extends IdentMethod {
	public static final String METHOD_NAME = "Name";
	public static final String METHOD_PREFIX = "n";
	
	public NameIdentMethod(IdentService service, IdentMethodFactory factory) {
		super(service, factory, METHOD_NAME, METHOD_PREFIX);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getForUser(User user) {
		return user.getNick();
	}
	
	public static class Factory extends IdentMethodFactory {
		public Factory() {
			super(METHOD_NAME, METHOD_PREFIX);
		}

		@Override
		public IdentMethod create(IdentService service) {
			return new NameIdentMethod(service, this);
		}
	}
}