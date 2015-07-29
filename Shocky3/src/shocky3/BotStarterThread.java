package shocky3;

import java.nio.charset.Charset;
import org.pircbotx.Configuration;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import shocky3.pircbotx.Bot;
import shocky3.pircbotx.CustomInputParser;
import shocky3.util.Synced;

public class BotStarterThread extends Thread {
	public final Shocky botApp;
	public final BotManager manager;
	
	public Bot bot = null;
	public boolean drop = false;
	
	public BotStarterThread(BotManager manager) {
		this.botApp = manager.botApp;
		this.manager = manager;
	}
	
	public void run() {
		try {
			Configuration.Builder cfgb = new Configuration.Builder()
				.setBotFactory(new BotFactory(){
					public InputParser createInputParser(PircBotX bot) {
						return new CustomInputParser(bot);
					}
				})
				.setEncoding(Charset.forName("UTF-8"))
				.setName(manager.botName)
				.setAutoNickChange(true)
				.setMessageDelay(manager.messageDelay)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("extended-join", true))
				.addCapHandler(new EnableCapHandler("account-notify", true))
				.setAutoReconnect(true)
				.addServer(manager.host)
				.addListener(new BotListener(this));
			Synced.forEach(botApp.pluginManager.plugins, plugin -> {
				if (plugin instanceof ListenerPlugin)
					cfgb.addListener(((ListenerPlugin)plugin).listener);
			});
			
			bot = new Bot(cfgb.buildConfiguration(), manager);
			bot.startBot();
		} catch (Exception e) {
			this.bot = null;
			drop = true;
			e.printStackTrace();
		}
	}
}