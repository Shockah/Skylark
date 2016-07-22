package io.shockah.skylark.history.db;

import java.util.Date;
import java.util.stream.Collectors;
import org.pircbotx.hooks.Event;
import org.pircbotx.hooks.events.JoinEvent;
import org.pircbotx.hooks.events.KickEvent;
import org.pircbotx.hooks.events.MessageEvent;
import org.pircbotx.hooks.events.NickChangeEvent;
import org.pircbotx.hooks.events.NoticeEvent;
import org.pircbotx.hooks.events.PartEvent;
import org.pircbotx.hooks.events.QuitEvent;
import org.pircbotx.hooks.types.GenericChannelUserEvent;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.skylark.Bot;
import io.shockah.skylark.db.DatabaseManager;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.history.HistoryPlugin;

@DatabaseTable(tableName = "io_shockah_skylark_history_line")
public class Line extends DbObject<Line> {
	public static final String SERVER_COLUMN = "server";
	public static final String CHANNEL_COLUMN = "channel";
	public static final String TYPE_COLUMN = "type";
	public static final String CONTENT_COLUMN = "content";
	public static final String DATE_COLUMN = "date";
	public static final String NICK_COLUMN = "nick";
	public static final String NICK2_COLUMN = "nick2";
	
	public static enum Type {
		Message, Notice, Join, Part, Quit, NickChange, Kick;
	}
	
	@DatabaseField(canBeNull = false, columnName = SERVER_COLUMN)
	public String server;
	
	@DatabaseField(canBeNull = false, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = false, columnName = TYPE_COLUMN)
	public Type type;
	
	@DatabaseField(canBeNull = true, columnName = NICK_COLUMN)
	public String nick;
	
	@DatabaseField(canBeNull = true, columnName = NICK2_COLUMN)
	public String nick2;
	
	@DatabaseField(dataType = DataType.LONG_STRING, canBeNull = true, columnName = CONTENT_COLUMN)
	public String content;
	
	@DatabaseField(canBeNull = false, columnName = DATE_COLUMN)
	public Date date;
	
	@Deprecated //ORMLite-only
	protected Line() {
	}
	
	public Line(Dao<Line, Integer> dao) {
		super(dao);
	}
	
	@Override
	public String toString() {
		return toString(0, 0, 0);
	}
	
	public String toString(Integer idColumnLength, Integer datetimeColumnLength, int nickLength) {
		StringBuilder sb = new StringBuilder();
		
		if (idColumnLength != null) {
			StringBuilder sb2 = new StringBuilder();
			sb2.append(String.format("#%d", getId()));
			while (sb2.length() < idColumnLength)
				sb2.insert(0, ' ');
			
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(sb2);
		}
		
		if (datetimeColumnLength != null) {
			HistoryPlugin plugin = getDatabaseManager().app.pluginManager.getPluginWithClass(HistoryPlugin.class);
			StringBuilder sb2 = new StringBuilder();
			sb2.append(plugin.dateFormat.format(date));
			while (sb2.length() < datetimeColumnLength)
				sb2.insert(0, ' ');
			
			if (sb.length() != 0)
				sb.append(' ');
			sb.append(String.format("[%s UTC]", sb2));
		}
		
		switch (type) {
			case Message:
			case Notice: {
				{
					String prefix, suffix;
					switch (type) {
						case Message:
							prefix = "<";
							suffix = ">";
							break;
						case Notice:
							prefix = suffix = "-";
							break;
						default:
							throw new IllegalArgumentException();
					}
					StringBuilder sb2 = new StringBuilder();
					sb2.append(String.format("%s%s%s", prefix, nick, suffix));
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(content);
			} break;
			case Join: {
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append("*");
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(String.format("%s has joined %s", nick, channel));
			} break;
			case Part: {
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append("*");
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(String.format("%s has left %s", nick, channel));
			} break;
			case Quit: {
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append("*");
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(String.format("%s has quit (%s)", nick, content));
			} break;
			case NickChange: {
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append("*");
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(String.format("%s is now known as %s", nick, nick2));
			} break;
			case Kick: {
				{
					StringBuilder sb2 = new StringBuilder();
					sb2.append("*");
					while (sb2.length() < nickLength)
						sb2.insert(0, ' ');
					
					if (sb.length() != 0)
						sb.append(' ');
					sb.append(sb2);
				}
				
				if (sb.length() != 0)
					sb.append(' ');
				sb.append(String.format("%s has kicked %s from %s (%s)", nick, nick2, channel, content));
			} break;
			default:
				throw new IllegalStateException();
		}
		
		return sb.toString();
	}
	
	private static void fillFromEvent(Line line, Event e) {
		line.server = e.<Bot>getBot().manager.name;
		line.date = new Date(e.getTimestamp());
	}
	
	private static void fillFromGenericChannelUserEvent(Line line, GenericChannelUserEvent e) {
		fillFromEvent(line, (Event)e);
		line.channel = e.getChannel().getName();
		line.nick = e.getUser().getNick();
	}
	
	public static Line createFrom(DatabaseManager manager, MessageEvent e) {
		return manager.create(Line.class, obj -> {
			fillFromGenericChannelUserEvent(obj, e);
			obj.type = Type.Message;
			obj.content = e.getMessage();
		});
	}
	
	public static Line createFrom(DatabaseManager manager, NoticeEvent e) {
		return manager.create(Line.class, obj -> {
			fillFromGenericChannelUserEvent(obj, e);
			obj.type = Type.Notice;
			obj.content = e.getMessage();
		});
	}
	
	public static Line createFrom(DatabaseManager manager, JoinEvent e) {
		return manager.create(Line.class, obj -> {
			fillFromGenericChannelUserEvent(obj, e);
			obj.type = Type.Join;
		});
	}
	
	public static Line createFrom(DatabaseManager manager, PartEvent e) {
		return manager.create(Line.class, obj -> {
			fillFromGenericChannelUserEvent(obj, e);
			obj.type = Type.Part;
		});
	}
	
	public static Line[] createFrom(DatabaseManager manager, QuitEvent e) {
		return e.getUser().getChannels().stream()
			.map(channel -> createFrom(manager, e, channel.getName()))
			.collect(Collectors.toList()).toArray(new Line[0]);
	}
	
	public static Line createFrom(DatabaseManager manager, QuitEvent e, String channel) {
		return manager.create(Line.class, obj -> {
			fillFromEvent(obj, e);
			obj.channel = channel;
			obj.nick = e.getUser().getNick();
			obj.content = e.getReason();
			obj.type = Type.Quit;
		});
	}
	
	public static Line[] createFrom(DatabaseManager manager, NickChangeEvent e) {
		return e.getUser().getChannels().stream()
			.map(channel -> createFrom(manager, e, channel.getName()))
			.collect(Collectors.toList()).toArray(new Line[0]);
	}
	
	public static Line createFrom(DatabaseManager manager, NickChangeEvent e, String channel) {
		return manager.create(Line.class, obj -> {
			fillFromEvent(obj, e);
			obj.channel = channel;
			obj.nick = e.getOldNick();
			obj.nick2 = e.getNewNick();
			obj.type = Type.Quit;
		});
	}
	
	public static Line createFrom(DatabaseManager manager, KickEvent e) {
		return manager.create(Line.class, obj -> {
			fillFromGenericChannelUserEvent(obj, e);
			obj.type = Type.Kick;
			obj.nick2 = e.getRecipient().getNick();
			obj.content = e.getReason();
		});
	}
}