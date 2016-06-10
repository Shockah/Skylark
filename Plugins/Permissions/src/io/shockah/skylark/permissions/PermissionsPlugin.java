package io.shockah.skylark.permissions;

import java.sql.SQLException;
import io.shockah.skylark.ident.IdentPlugin;
import io.shockah.skylark.permissions.db.Group;
import io.shockah.skylark.plugin.Plugin;
import io.shockah.skylark.plugin.PluginManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;

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
			groupsDao = DaoManager.createDao(manager.app.databaseManager.connection, Group.class);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public Dao<Group, String> getGroupsDao() {
		return groupsDao;
	}
}