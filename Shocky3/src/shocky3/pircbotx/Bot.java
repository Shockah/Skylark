package shocky3.pircbotx;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.User;
import shocky3.BotManager;
import shocky3.Shocky;

public class Bot extends PircBotX {
	public final Shocky botApp;
	public final BotManager manager;
	public final List<User> blockedDCC = Collections.synchronizedList(new LinkedList<User>());
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		botApp = manager.botApp;
		this.manager = manager;
	}
}