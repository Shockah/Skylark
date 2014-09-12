package surlannounce;

import java.util.Date;
import shocky3.BotManager;

public class Sender {
	public BotManager manager;
	public String channel, nick;
	public Date date;
	public int counter;
	
	public Sender(BotManager manager, String channel, String nick, Date date, int counter) {
		this.manager = manager;
		this.channel = channel;
		this.nick = nick;
		this.date = date;
		this.counter = counter;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof Sender)) return false;
		Sender s = (Sender)other;
		return manager.name.equals(s.manager.name) && channel.equals(s.channel);
	}
}