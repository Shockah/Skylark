package io.shockah.skylark;

import io.shockah.skylark.plugin.BotManagerService;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.Box;
import io.shockah.skylark.util.ReadWriteList;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import org.pircbotx.Configuration;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;

public class BotManager {
	public static final String CHANNELS_PER_CONNECTION_CAPABILITY = "CHANLIMIT";
	public static final Pattern CHANNELS_PER_CONNECTION_CAPABILITY_VALUE_PATTERN = Pattern.compile("\\#\\:([0-9]+)");
	
	public static final String DEFAULT_BOT_NAME = "Skylark";
	public static final long DEFAULT_MESSAGE_DELAY = 500;
	
	public final ServerManager serverManager;
	public final String name;
	public final String host;
	public final Integer port;
	
	public Integer channelsPerConnection;
	public long messageDelay = DEFAULT_MESSAGE_DELAY;
	public String botName = DEFAULT_BOT_NAME;
	
	public final ReadWriteList<Bot> bots = new ReadWriteList<>(new ArrayList<>());
	public final ReadWriteList<BotManagerService> services = new ReadWriteList<>(new ArrayList<>());
	
	public BotManager(ServerManager serverManager, String name, String host) {
		this(serverManager, name, host, null);
	}
	
	public BotManager(ServerManager serverManager, String name, String host, Integer port) {
		this.serverManager = serverManager;
		this.name = name;
		this.host = host;
		this.port = port;
		setupServices();
	}
	
	public void setupServices() {
		services.writeOperation(services -> {
			PluginManager pluginManager = serverManager.app.pluginManager;
			pluginManager.botManagerServiceFactories.iterate(factory -> {
				BotManagerService service = factory.createService(this);
				services.add(service);
				pluginManager.botManagerServices.add(service);
			});
		});
	}
	
	@SuppressWarnings("unchecked")
	public <T extends BotManagerService> T getService(Class<T> clazz) {
		return (T)services.findOne(service -> clazz.isInstance(service));
	}
	
	public int getChannelsPerConnection() {
		if (channelsPerConnection == null) {
			return bots.readOperation(bots -> {
				if (bots.isEmpty()) {
					//placeholder until we actually connect
					return 1;
				} else {
					/*Bot bot = bots.get(0);
					String capValue = bot.getEnabledCapabilityValue(CHANNELS_PER_CONNECTION_CAPABILITY);
					if (capValue != null) {
						Matcher m = CHANNELS_PER_CONNECTION_CAPABILITY_VALUE_PATTERN.matcher(capValue);
						if (m.find())
							return Integer.parseInt(m.group(1));
					}*/
					return Integer.MAX_VALUE;
				}
			});
		} else {
			return channelsPerConnection;
		}
	}
	
	public Bot joinChannel(String channelName) {
		return bots.writeOperation(bots -> {
			int channelsPerConnection = getChannelsPerConnection();
			for (Bot bot : bots) {
				if (bot.getUserBot().getChannels().size() < channelsPerConnection) {
					bot.sendIRC().joinChannel(channelName);
					return bot;
				}
			}
			
			Bot bot = connectNewBot();
			bot.sendIRC().joinChannel(channelName);
			return bot;
		});
	}
	
	public Bot connectNewBot() {
		Box<Bot> newBotBox = new Box<>();
		CountDownLatch latch = new CountDownLatch(1);
		
		new Thread(){
			@Override
			public void run() {
				try {
					newBotBox.value = buildNewBot(latch);
					newBotBox.value.startBot();
				} catch (Exception e) {
					newBotBox.value = null;
					latch.countDown();
				}
			}
		}.start();
		
		try {
			latch.await();
		} catch (InterruptedException e) {
		}
		
		if (newBotBox.value != null)
			bots.add(newBotBox.value);
		return newBotBox.value;
	}
	
	protected Bot buildNewBot(CountDownLatch latch) {
		Configuration.Builder cfgb = new Configuration.Builder()
			.setBotFactory(new BotFactory(){
				@Override
				public InputParser createInputParser(PircBotX bot) {
					return new SkylarkInputParser(bot);
				}
			})
			.setEncoding(Charset.forName("UTF-8"))
			.setName(name)
			.setAutoNickChange(true)
			.setMessageDelay(messageDelay)
			.setCapEnabled(true)
			.addCapHandler(new EnableCapHandler("extended-join", true))
			.addCapHandler(new EnableCapHandler("account-notify", true))
			.setAutoReconnect(true)
			.addListener(new ListenerAdapter(){
				@Override
				public void onConnect(ConnectEvent event) throws Exception {
					latch.countDown();
				}
			});
		
		if (port == null)
			cfgb.addServer(host);
		else
			cfgb.addServer(host, port);
		
		PluginManager pluginManager = serverManager.app.pluginManager;
		pluginManager.plugins.iterate(plugin -> {
			if (plugin instanceof ListenerPlugin)
				cfgb.addListener(((ListenerPlugin)plugin).listener);
		});
		
		return new Bot(cfgb.buildConfiguration(), this);
	}
}