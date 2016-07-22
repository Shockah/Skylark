package io.shockah.skylark.history;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.pircbotx.Channel;
import io.shockah.skylark.Bot;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.history.db.Line;

public class ContextHistoryQuery extends AbstractHistoryQuery {
	public final int baseLineId;
	public Integer lineCountContext;
	public Integer secondsContext;
	
	public ContextHistoryQuery(Channel channel, int baseLineId) {
		super(channel);
		this.baseLineId = baseLineId;
	}
	
	@Override
	public List<Line> query(HistoryPlugin plugin) {
		if (lineCountContext == null && secondsContext == null)
			throw new IllegalArgumentException("You must specify either the line count or timeframe.");
		
		Line baseLine = plugin.getLine(channel, baseLineId);
		if (baseLine == null)
			throw new IllegalArgumentException(String.format("No valid line with ID %d found.", baseLineId));
		
		List<Line> prev = plugin.manager.app.databaseManager.query(Line.class, (qb, where) -> {
			where.equals(Line.SERVER_COLUMN, channel.<Bot>getBot().manager.name);
			where.equals(Line.CHANNEL_COLUMN, channel.getName());
			where.less(DbObject.ID_COLUMN, baseLine.getId());
			
			if (secondsContext != null)
				where.greaterOrEqual(Line.DATE_COLUMN, new Date(baseLine.date.getTime() - secondsContext * 1000l));
			
			qb.orderBy(DbObject.ID_COLUMN, false);
			
			if (lineCountContext != null)
				qb.limit((long)lineCountContext);
		});
		Collections.reverse(prev);
		
		List<Line> next = plugin.manager.app.databaseManager.query(Line.class, (qb, where) -> {
			where.equals(Line.SERVER_COLUMN, channel.<Bot>getBot().manager.name);
			where.equals(Line.CHANNEL_COLUMN, channel.getName());
			where.greater(DbObject.ID_COLUMN, baseLine.getId());
			
			if (secondsContext != null)
				where.lessOrEqual(Line.DATE_COLUMN, new Date(baseLine.date.getTime() + secondsContext * 1000l));
			
			qb.orderBy(DbObject.ID_COLUMN, true);
			
			if (lineCountContext != null)
				qb.limit((long)lineCountContext);
		});
		
		List<Line> results = new ArrayList<>();
		results.addAll(prev);
		results.add(baseLine);
		results.addAll(next);
		return results;
	}
}