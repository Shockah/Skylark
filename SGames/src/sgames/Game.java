package sgames;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.User;

public abstract class Game {
	public final Plugin plugin;
	public final List<User> users;
	
	public Game(Plugin plugin, List<User> users) {
		this.plugin = plugin;
		this.users = Collections.unmodifiableList(new LinkedList<User>(users));
	}
	
	public abstract void finish();
}