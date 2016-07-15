package io.shockah.skylark;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import org.pircbotx.Colors;
import org.pircbotx.Configuration;
import org.pircbotx.Configuration.BotFactory;
import org.pircbotx.InputParser;
import org.pircbotx.PircBotX;
import org.pircbotx.cap.EnableCapHandler;
import org.pircbotx.hooks.ListenerAdapter;
import org.pircbotx.hooks.events.ConnectEvent;
import io.shockah.skylark.db.Server;
import io.shockah.skylark.plugin.BotManagerService;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;
import io.shockah.skylark.util.Box;
import io.shockah.skylark.util.ReadWriteList;
import io.shockah.skylark.util.StringUtils;

public class BotManager {
	public static final Charset CHARSET = Charset.forName("UTF-8");
	public static final String CHANNELS_PER_CONNECTION_CAPABILITY = "CHANLIMIT";
	public static final Pattern CHANNELS_PER_CONNECTION_CAPABILITY_VALUE_PATTERN = Pattern.compile("\\#\\:([0-9]+)");
	
	public static final String DEFAULT_ELLIPSIS = "�";
	public static final String DEFAULT_BOT_NAME = "Skylark";
	public static final long DEFAULT_MESSAGE_DELAY = 500;
	public static final int DEFAULT_LINEBREAK_LENGTH = 400;
	
	public final ServerManager serverManager;
	public final String name;
	public final String host;
	public final Integer port;
	
	public Integer channelsPerConnection;
	public long messageDelay = DEFAULT_MESSAGE_DELAY;
	public String botName = DEFAULT_BOT_NAME;
	public Integer linebreakLength;
	public String ellipsis;
	
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
		linebreakLength = serverManager.app.config.getObjectOrEmpty("messages").getOptionalInt("linebreakLength");
		ellipsis = serverManager.app.config.getObjectOrEmpty("messages").getString("ellipsis", null);
		setupServices();
	}
	
	public BotManager(ServerManager serverManager, Server server) {
		this(serverManager, server.name, server.host);
		channelsPerConnection = server.channelsPerConnection;
		messageDelay = server.messageDelay == null ? BotManager.DEFAULT_MESSAGE_DELAY : server.messageDelay;
		botName = server.botName == null ? BotManager.DEFAULT_BOT_NAME : server.botName;
		if (server.linebreakLength != null)
			linebreakLength = server.linebreakLength;
		if (server.ellipsis != null)
			ellipsis = server.ellipsis;
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
		return (T)services.filterFirst(service -> clazz.isInstance(service));
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
	
	public int getLinebreakLength() {
		return linebreakLength == null ? DEFAULT_LINEBREAK_LENGTH : linebreakLength;
	}
	
	public String getEllipsis() {
		return ellipsis == null ? DEFAULT_ELLIPSIS : ellipsis;
	}
	
	public Bot getAnyBot() {
		return bots.readOperation(bots -> {
			if (bots.isEmpty())
				return connectNewBot();
			return bots.get(0);
		});
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
	
	public Channel getChannel(String channelName) {
		return bots.firstResult(bot -> {
			for (Channel channel : bot.getUserBot().getChannels()) {
				if (channel.getName().equals(channelName))
					return channel;
			}
			return null;
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
			.setEncoding(CHARSET)
			.setName(botName)
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
	
	public List<String> linebreakIfNeeded(List<String> lines) {
		return linebreakIfNeeded(lines, null, getEllipsis());
	}
	
	public List<String> linebreakIfNeeded(List<String> lines, Integer maxLines) {
		return linebreakIfNeeded(lines, maxLines, getEllipsis());
	}
	
	public List<String> linebreakIfNeeded(List<String> lines, Integer maxLines, String ellipsis) {
		if (maxLines != null && maxLines < 1)
			throw new IllegalArgumentException();
		
		List<String> newLines = new ArrayList<>();
		for (String line : lines) {
			newLines.addAll(linebreakIfNeeded(line));
		}
		
		if (maxLines != null && newLines.size() > maxLines) {
			newLines = newLines.subList(0, maxLines);
			if (ellipsis != null && !ellipsis.isEmpty()) {
				String lastLine = newLines.get(newLines.size() - 1);
				String replacement = ellipsis;
				if (Colors.removeFormattingAndColors(lastLine).equals(lastLine))
					replacement = "\u000F" + replacement;
				
				if (replacement.length() >= lastLine.length())
					lastLine = replacement;
				else
					lastLine = lastLine.substring(0, lastLine.length() - replacement.length()) + replacement;
				newLines.set(newLines.size() - 1, lastLine);
			}
		}
		
		return newLines;
	}
	
	public List<String> linebreakIfNeeded(String line) {
		List<String> list = new ArrayList<>();
		
		while (true) {
			String trimmedMessage = StringUtils.trimToByteLength(line, getLinebreakLength(), CHARSET);
			list.add(trimmedMessage);
			
			if (trimmedMessage.equals(line)) {
				break;
			} else {
				line = line.substring(trimmedMessage.length());
			}
		}
		
		return list;
	}
}