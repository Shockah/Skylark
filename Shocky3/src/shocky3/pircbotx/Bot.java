package shocky3.pircbotx;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import shocky3.BotManager;
import shocky3.Shocky;

public class Bot extends PircBotX {
	public final Shocky botApp;
	public final BotManager manager;
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		botApp = manager.botApp;
		this.manager = manager;
	}
}