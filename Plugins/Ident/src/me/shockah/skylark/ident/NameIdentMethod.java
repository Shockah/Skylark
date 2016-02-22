package me.shockah.skylark.ident;

import org.pircbotx.User;

public class NameIdentMethod extends IdentMethod {
	public NameIdentMethod(IdentService service) {
		super(service, "Name", "n");
	}

	@Override
	public boolean isAvailable() {
		return true;
	}

	@Override
	public String getForUser(User user) {
		return user.getNick();
	}
}