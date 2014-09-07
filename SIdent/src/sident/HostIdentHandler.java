package sident;

import org.pircbotx.User;
import shocky3.BotManager;

public class HostIdentHandler extends IdentHandler {
	public HostIdentHandler() {
		this(null);
	}
	public HostIdentHandler(BotManager manager) {
		super(manager, "h", "hostmask", OVERHEAD_LOW, (CREDIBILITY_LOW + CREDIBILITY_MEDIUM) / 2);
	}
	
	public IdentHandler copy(BotManager manager) {
		return new HostIdentHandler(manager);
	}
	
	public boolean checkAvailability() {
		return true;
	}
	
	public String account(User user) {
		return user.getHostmask();
	}
	
	public boolean isAccount(User user, String account) {
		String acc = account(user);
		return acc.equals(account);
	}
}