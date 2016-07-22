package io.shockah.skylark.history;

import java.util.List;
import org.pircbotx.Channel;
import io.shockah.skylark.history.db.Line;

public abstract class AbstractHistoryQuery {
	public final Channel channel;
	
	public AbstractHistoryQuery(Channel channel) {
		this.channel = channel;
	}
	
	public abstract List<Line> query(HistoryPlugin plugin);
}