package me.shockah.skylark.ident;

import me.shockah.skylark.Bot;
import org.pircbotx.User;

public class ServerIdentMethod extends IdentMethod {
	public ServerIdentMethod(IdentService service) {
		super(service, "Server", "srv");
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
}