package scommands;

import org.pircbotx.Channel;
import org.pircbotx.User;
import shocky3.pircbotx.event.GenericUserMessageEvent;

public final class TargetedText {
	public static enum Type {
		Message, Notice;
	}
	public static enum TextType {
		Text, Action;
	}
	
	public String text;
	public Channel channel = null;
	public User user = null;
	public Type type = Type.Message;
	public TextType textType = TextType.Text;
	
	public TargetedText(GenericUserMessageEvent e) {
		this(e.getMessage(), e.getChannel(), e.getUser());
	}
	public TargetedText(String text, Channel channel) {
		this(text, channel, null);
	}
	public TargetedText(String text, User user) {
		this(text, null, user);
	}
	public TargetedText(String text, Channel channel, User user) {
		this.text = text;
		this.channel = channel;
		this.user = user;
	}
	
	public TargetedText text(String text) {
		this.text = text;
		return this;
	}
	public TargetedText channel(Channel channel) {
		this.channel = channel;
		return this;
	}
	public TargetedText user(User user) {
		this.user = user;
		return this;
	}
	public TargetedText type(Type type) {
		this.type = type;
		return this;
	}
	public TargetedText textType(TextType textType) {
		this.textType = textType;
		return this;
	}
	
	public void send() {
		if (channel != null) {
			StringBuilder sb = new StringBuilder();
			if (user != null && textType != TextType.Action)
				sb.append(String.format("%s: ", user.getNick()));
			sb.append(text);
			
			if (textType == TextType.Action) {
				sb.insert(0, "\u0001ACTION");
				sb.append("\u0001");
			}
			
			switch (type) {
				case Message:
					channel.send().message(sb.toString());
					break;
				case Notice:
					channel.send().notice(sb.toString());
					break;
			}
		} else if (user != null) {
			StringBuilder sb = new StringBuilder();
			sb.append(text);
			
			if (textType == TextType.Action) {
				sb.insert(0, "\u0001ACTION");
				sb.append("\u0001");
			}
			
			switch (type) {
				case Message:
					user.send().message(sb.toString());
					break;
				case Notice:
					user.send().notice(sb.toString());
					break;
			}
		} else {
			throw new RuntimeException("No target specified for the message:\n" + text);
		}
	}
}