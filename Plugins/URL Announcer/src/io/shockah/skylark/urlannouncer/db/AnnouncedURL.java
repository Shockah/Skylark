package io.shockah.skylark.urlannouncer.db;

import java.util.Date;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.skylark.db.DbObject;

@DatabaseTable(tableName = "io_shockah_skylark_urlannouncer_announcedurl")
public class AnnouncedURL extends DbObject<AnnouncedURL> {
	public static final String SERVER_COLUMN = "server";
	public static final String CHANNEL_COLUMN = "channel";
	public static final String URL_COLUMN = "url";
	
	@DatabaseField(canBeNull = false, columnName = SERVER_COLUMN)
	public String server;
	
	@DatabaseField(canBeNull = false, columnName = CHANNEL_COLUMN)
	public String channel;
	
	@DatabaseField(canBeNull = false, columnName = URL_COLUMN)
	public String url;
	
	@DatabaseField(canBeNull = false)
	public Date date;
	
	@DatabaseField(canBeNull = false)
	public int counter;
	
	@DatabaseField(canBeNull = false)
	public String nick;
	
	@Deprecated //ORMLite-only
	protected AnnouncedURL() {
	}
	
	public AnnouncedURL(Dao<AnnouncedURL, Integer> dao) {
		super(dao);
		date = new Date();
		counter = 1;
	}
}