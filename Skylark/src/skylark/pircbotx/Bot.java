package skylark.pircbotx;

import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import skylark.BotManager;
import skylark.Skylark;

public class Bot extends PircBotX {
	public final Skylark botApp;
	public final BotManager manager;
	
	public Bot(Configuration configuration, BotManager manager) {
		super(configuration);
		botApp = manager.botApp;
		this.manager = manager;
	}
}