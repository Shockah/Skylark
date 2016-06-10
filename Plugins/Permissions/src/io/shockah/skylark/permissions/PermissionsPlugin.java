package io.shockah.skylark.permissions;

import io.shockah.skylark.ident.IdentPlugin;
import io.shockah.skylark.permissions.db.Group;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class PermissionsPlugin extends Plugin {
	@Dependency
	protected IdentPlugin identPlugin;
	
	private Dao<Group, String> groupsDao;
	
	public PermissionsPlugin(PluginManager manager, Info info) {
		super(manager, info);
	}
	
	@Override
	protected void onLoad() {
		try {
			ConnectionSource connection = manager.app.databaseManager.connection;
			
			groupsDao = DaoManager.createDao(connection, Group.class);
			TableUtils.createTableIfNotExists(connection, Group.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Dao<Group, String> getGroupsDao() {
		return groupsDao;
	}
}