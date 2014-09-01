package shocky3.ident;

import org.pircbotx.User;
import shocky3.BotManager;

public class ServerIdentHandler extends IdentHandler {
	public ServerIdentHandler() {
		this(null);
	}
	public ServerIdentHandler(BotManager manager) {
		super(manager, "srv", "server", IdentHandler.OVERHEAD_LOW);
	}
	
	public IdentHandler copy(BotManager manager) {
		return new ServerIdentHandler(manager);
	}
	
	public boolean checkAvailability() {
		return true;
	}
	
	public String account(User user) {
		return String.format("%s (%s)", manager.name, manager.host);
	}
	
	public boolean isAccount(User user, String account) {
		String acc = account(user);
		return acc.equals(account);
	}
}