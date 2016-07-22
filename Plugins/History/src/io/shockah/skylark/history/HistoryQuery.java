package io.shockah.skylark.history;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import org.pircbotx.Channel;
import com.j256.ormlite.stmt.Where;
import io.shockah.skylark.Bot;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.history.db.Line;

public class HistoryQuery extends AbstractHistoryQuery {
	public String nick;
	public Integer lines;
	public Integer seconds;
	public Pattern pattern;
	public boolean fromStart = false;
	
	public HistoryQuery(Channel channel) {
		super(channel);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<Line> query(HistoryPlugin plugin) {
		if (lines == null && seconds == null)
			throw new IllegalArgumentException("You must specify either the line count or timeframe.");
		
		Line firstLine;
		if (fromStart && seconds != null) {
			firstLine = plugin.manager.app.databaseManager.queryFirst(Line.class, (qb, where) -> {
				where.equals(Line.SERVER_COLUMN, channel.<Bot>getBot().manager.name);
				where.equals(Line.CHANNEL_COLUMN, channel.getName());
				qb.orderBy(DbObject.ID_COLUMN, true);
			});
		} else {
			firstLine = null;
		}
		
		List<Line> results = plugin.manager.app.databaseManager.query(Line.class, qb -> {
			Where<Line, Integer> where = qb.where();
			
			where.eq(Line.SERVER_COLUMN, channel.<Bot>getBot().manager.name);
			where.and();
			where.eq(Line.CHANNEL_COLUMN, channel.getName());
			
			if (nick != null) {
				where.and();
				where.or(
					where.eq(Line.NICK_COLUMN, nick),
					where.eq(Line.NICK2_COLUMN, nick)
				);
			}
			
			if (pattern != null) {
				where.and();
				where.rawComparison(Line.CONTENT_COLUMN, "REGEXP", pattern.pattern());
			}
			
			if (seconds != null) {
				if (fromStart) {
					if (firstLine != null) {
						where.and();
						where.le(Line.DATE_COLUMN, new Date(firstLine.date.getTime() + seconds * 1000l));
					}
				} else {
					where.and();
					where.ge(Line.DATE_COLUMN, new Date(new Date().getTime() - seconds * 1000l));
				}
			}
			
			qb.orderBy(DbObject.ID_COLUMN, fromStart);
			
			if (lines != null)
				qb.limit((long)lines);
		});
		if (!fromStart)
			Collections.reverse(results);
		return results;
	}
}