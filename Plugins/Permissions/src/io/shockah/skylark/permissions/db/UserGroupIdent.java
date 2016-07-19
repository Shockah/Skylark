package io.shockah.skylark.permissions.db;

import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.ident.IdentMethod;
import java.sql.SQLException;
import java.util.regex.Pattern;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_usergroupidents")
public class UserGroupIdent extends DbObject<UserGroupIdent> {
	public static final String METHOD_COLUMN = "method";
	public static final String IDENT_PATTERN_COLUMN = "ident_pattern";
	public static final String USERGROUP_COLUMN = "usergroup_id";
	
	@DatabaseField(canBeNull = false, columnName = METHOD_COLUMN)
	public String method;
	
	@DatabaseField(canBeNull = false, columnName = IDENT_PATTERN_COLUMN)
	public Pattern identPattern;
	
	@DatabaseField(foreign = true, canBeNull = false, columnName = USERGROUP_COLUMN)
	private UserGroup userGroup;
	
	@Deprecated //ORMLite-only
	protected UserGroupIdent() {
		super();
	}
	
	@Deprecated //ORMLite-only
	public UserGroupIdent(Dao<UserGroupIdent, Integer> dao) {
		super(dao);
	}
	
	public UserGroupIdent(Dao<UserGroupIdent, Integer> dao, UserGroup userGroup) {
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
	
	public boolean identMatches(IdentMethod method, String identString) {
		if (!method.prefix.equals(method))
			return false;
		return identPattern.matcher(identString).find();
	}
}