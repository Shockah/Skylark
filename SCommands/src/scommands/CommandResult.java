package scommands;

import java.util.ArrayList;
import java.util.List;
import org.pircbotx.Channel;
import org.pircbotx.User;

public class CommandResult {
	public static enum Type {
		Message, Action, Notice
	}
	
	public static class Line {
		public Type type = Type.Message;
		public boolean ping = true;
		public User user;
		public Channel channel;
		public String text;
		
		public Line(String text) {
			this.text = text;
		}
		
		public String formatted() {
			if (ping && user != null)
				return String.format("%s: %s", user.getNick(), text);
			return text;
		}
		
		public void send() {
			if (type == null) return;
			switch (type) {
				case Message:
					if (channel != null)
						channel.send().message(formatted());
					else
						user.send().message(formatted());
					break;
				case Action:
					if (channel != null)
						channel.send().action(formatted());
					else
						user.send().action(formatted());
					break;
				case Notice:
					if (channel != null)
						channel.send().notice(formatted());
					else
						user.send().notice(formatted());
					break;
			}
		}
	}
	
	public final List<Line> lines = new ArrayList<>();
	public final User defaultUser;
	public final Channel defaultChannel;
	public boolean quiet = false;
	
	public CommandResult() {
		this(null, null);
	}
	public CommandResult(User defaultUser, Channel defaultChannel) {
		this.defaultUser = defaultUser;
		this.defaultChannel = defaultChannel;
	}
	
	public Line add(String text) {
		return add(Type.Message, text);
	}
	public Line add(Type type, String text) {
		return add(defaultUser, defaultChannel, type, text);
	}
	public Line add(User user, Channel channel, Type type, String text) {
		Line line = new Line(text);
		line.type = type;
		if (type != Type.Message)
			line.ping = false;
		line.user = user;
		line.channel = channel;
		lines.add(line);
		return line;
	}
	
	public void ping(boolean ping) {
		for (Line line : lines)
			line.ping = ping;
	}
	public void changeType(Type type) {
		for (Line line : lines)
			line.type = type;
	}
	
	public void send() {
		if (quiet) return;
		for (Line line : lines)
			line.send();
	}
}