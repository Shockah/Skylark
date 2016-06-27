package io.shockah.skylark.permissions.db;

import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.db.DbObject;
import java.sql.SQLException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_usergrouppermissions")
public class UserGroupPermission extends DbObject<UserGroupPermission, Integer> {
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(canBeNull = false)
	public String permission;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	protected UserGroupPermission() {
	}
	
	public UserGroupPermission(Dao<UserGroupPermission, Integer> dao, UserGroup userGroup) {
		super(dao);
		this.userGroup = userGroup;
	}
	
	public UserGroup getUserGroup() throws SQLException {
		try {
			if (userGroup != null)
				userGroup.refresh();
			return userGroup;
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
	}
	
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
	
	public boolean permissionGranted(String actionPermission) {
		String[] spl = permission.split("\\.");
		String[] splArg = actionPermission.split("\\.");
		
		if (spl.length > splArg.length)
			return false;
		
		for (int i = 0; i < spl.length; i++) {
			String s = spl[i];
			String arg = splArg[i];
			
			if (s.equals("*"))
				return true;
			if (!s.equals(arg))
				return false;
		}
		return true;
	}
}