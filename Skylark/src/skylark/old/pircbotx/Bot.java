package skylark.old.pircbotx;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import skylark.old.BotManager;
import skylark.old.Skylark;
import skylark.old.WhoisManager;

public class Bot extends PircBotX {
	public final Skylark botApp;
	public final BotManager manager;
	public final WhoisManager whoisManager;
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		botApp = manager.botApp;
		this.manager = manager;
		whoisManager = new WhoisManager(this);
	}
}