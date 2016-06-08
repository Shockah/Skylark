package me.shockah.skylark.ident;

import me.shockah.skylark.Bot;
import org.pircbotx.User;

public class ServerIdentMethod extends IdentMethod {
	public static final String METHOD_NAME = "Server";
	public static final String METHOD_PREFIX = "srv";
	
	public ServerIdentMethod(IdentService service, IdentMethodFactory factory) {
		super(service, factory, METHOD_NAME, METHOD_PREFIX);
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getForUser(User user) {
		Bot bot = user.getBot();
		return bot.manager.name;
	}
	
	public static class Factory extends IdentMethodFactory {
		public Factory() {
			super(METHOD_NAME, METHOD_PREFIX);
		}

		@Override
		public IdentMethod create(IdentService service) {
			return new ServerIdentMethod(service, this);
		}
	}
}