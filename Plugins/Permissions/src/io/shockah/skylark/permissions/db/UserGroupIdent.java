package io.shockah.skylark.permissions.db;

import java.sql.SQLException;
import java.util.regex.Pattern;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.skylark.ident.IdentMethod;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_usergroupidents")
public class UserGroupIdent extends BaseDaoEnabled<UserGroupIdent, Integer> {
	public static final String METHOD_COLUMN = "method";
	public static final String IDENT_PATTERN_COLUMN = "ident_pattern";
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField(columnName = METHOD_COLUMN)
	public String method;
	
	@DatabaseField(columnName = IDENT_PATTERN_COLUMN)
	public Pattern identPattern;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	UserGroupIdent() {
	}
	
	public UserGroupIdent(Dao<UserGroupIdent, Integer> dao, UserGroup userGroup) {
		setDao(dao);
		this.userGroup = userGroup;
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof UserGroupIdent))
			return false;
		UserGroupIdent ident = (UserGroupIdent)other;
		return id == ident.id;
	}
	
	public int getId() {
		return id;
	}
	
	public UserGroup getUserGroup() throws SQLException {
		if (userGroup != null)
			userGroup.refresh();
		return userGroup;
	}
	
	public void setUserGroup(UserGroup userGroup) {
		this.userGroup = userGroup;
	}
	
	public boolean identMatches(IdentMethod method, String identString) {
		if (!method.prefix.equals(method))
			return false;
		return identPattern.matcher(identString).find();
	}
}