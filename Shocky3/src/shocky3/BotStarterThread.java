package shocky3;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.Charset;
import org.pircbotx.Configuration;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.Listener;
import org.pircbotx.hooks.events.ConnectEvent;
import shocky3.pircbotx.CustomInputParser;

public class BotStarterThread extends Thread {
	public final Shocky botApp;
	public final BotManager manager;
	
	public PircBotX bot = null;
	public boolean drop = false;
	
	public BotStarterThread(BotManager manager) {
		this.botApp = manager.botApp;
		this.manager = manager;
	}
	
	public void run() {
		try {
			Configuration.Builder<PircBotX> cfgb = new Configuration.Builder<>()
				.setEncoding(Charset.forName("UTF-8"))
				.setName(manager.botName)
				.setAutoNickChange(true)
				.setServerHostname(manager.host)
				.setMessageDelay(manager.messageDelay)
				.setCapEnabled(true)
				.addCapHandler(new EnableCapHandler("extended-join", true))
				.addCapHandler(new EnableCapHandler("account-notify", true))
				.addListener(new Listener<PircBotX>(){
					public void onEvent(Event<PircBotX> e) throws Exception {
						if (e instanceof ConnectEvent) {
							BotStarterThread.this.bot = e.getBot();
							drop = true;
						}
					}
				}
			);
			for (Plugin plugin : botApp.pluginManager.plugins()) {
				if (plugin instanceof ListenerPlugin) {
					cfgb.addListener(((ListenerPlugin)plugin).listener);
				}
			}
			
			bot = new PircBotX(cfgb.buildConfiguration());
			
			try {
				Field field = bot.getClass().getDeclaredField("inputParser");
				field.setAccessible(true);
				
				Field mfield = Field.class.getDeclaredField("modifiers");
				mfield.setAccessible(true);
				mfield.setInt(field, field.getModifiers() & ~Modifier.FINAL);
				
				field.set(bot, new CustomInputParser(bot));
				
				mfield.setInt(field, field.getModifiers() | Modifier.FINAL);
			} catch (Exception e) {e.printStackTrace();}
			
			bot.startBot();
		} catch (Exception e) {
			this.bot = null;
			drop = true;
			e.printStackTrace();
		}
	}
}