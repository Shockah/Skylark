package io.shockah.skylark.history;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import org.pircbotx.Channel;
import org.pircbotx.hooks.events.ActionEvent;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.ModeEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.events.UserModeEvent;
import io.shockah.skylark.Bot;
import io.shockah.skylark.commands.CommandsPlugin;
import io.shockah.skylark.event.OutActionEvent;
import io.shockah.skylark.event.OutMessageEvent;
import io.shockah.skylark.event.OutNoticeEvent;
import io.shockah.skylark.history.db.Line;
import io.shockah.skylark.plugin.ListenerPlugin;
import io.shockah.skylark.plugin.PluginManager;

public class HistoryPlugin extends ListenerPlugin {
	@Dependency
	protected CommandsPlugin commandsPlugin;
	
	private HistoryCommand command;
	
	public SimpleDateFormat dateFormat;
	
	public HistoryPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
		
		commandsPlugin.addNamedCommand(command = new HistoryCommand(this));
	}
	
	@Override
	protected void onUnload() {
		commandsPlugin.removeNamedCommand(command);
	}
	
	public Line getLine(Channel channel, int id) {
		Line line = manager.app.databaseManager.get(Line.class, id);
		String server = channel.<Bot>getBot().manager.name;
		String channelName = channel.getName();
		return line.server.equals(server) && line.channel.equals(channelName) ? line : null;
	}
	
	@Override
	protected void onMessage(MessageEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onOutMessage(OutMessageEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onAction(ActionEvent e) {
		if (e.getChannel() != null)
			Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onOutAction(OutActionEvent e) {
		if (e.getChannel() != null)
			Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onNotice(NoticeEvent e) {
		if (e.getChannel() != null)
			Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onOutNotice(OutNoticeEvent e) {
		if (e.getChannel() != null)
			Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onJoin(JoinEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onPart(PartEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onQuit(QuitEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onNickChange(NickChangeEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onKick(KickEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onMode(ModeEvent e) {
		if (e.getUser() != null || e.getUserHostmask() != null)
			Line.createFrom(manager.app.databaseManager, e);
	}
	
	@Override
	protected void onUserMode(UserModeEvent e) {
		Line.createFrom(manager.app.databaseManager, e);
	}
}