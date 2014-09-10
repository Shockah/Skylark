package surlannounce;

import java.util.Date;
import shocky3.BotManager;

public class Sender {
	public BotManager manager;
	public String channel, nick;
	public Date date;
	
	public Sender(BotManager manager, String channel, String nick, Date date) {
		this.manager = manager;
		this.channel = channel;
		this.nick = nick;
		this.date = date;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Sender)) return false;
		Sender s = (Sender)other;
		return manager.name.equals(s.manager.name) && channel.equals(s.channel);
	}
}