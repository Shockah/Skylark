package io.shockah.skylark.permissions.db;

import java.io.IOException;
import java.util.Objects;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.misc.BaseDaoEnabled;
import com.j256.ormlite.table.DatabaseTable;
import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.ident.IdentMethod;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_usergroups")
public class UserGroup extends BaseDaoEnabled<UserGroup, Integer> {
	@DatabaseField(generatedId = true)
	private int id;
	
	@DatabaseField
	public String name;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupIdent> idents;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupPermission> permissions;
	
	@Deprecated //ORMLite-only
	UserGroup() {
	}
	
	public UserGroup(Dao<UserGroup, Integer> dao) {
		setDao(dao);
	}
	
	public boolean equals(Object other) {
		if (!(other instanceof UserGroup))
			return false;
		UserGroup group = (UserGroup)other;
		return Objects.equals(name, group.name);
	}
	
	public int getId() {
		return id;
	}
	
	public ForeignCollection<UserGroupIdent> getIdents() {
		return idents;
	}
	
	public ForeignCollection<UserGroupPermission> getPermissions() {
		return permissions;
	}
	
	public boolean identBelongs(IdentMethod method, String identString) {
		try (CloseableWrappedIterable<UserGroupIdent> cIdents = idents.getWrappedIterable()) {
			for (UserGroupIdent ident : cIdents) {
				if (ident.identMatches(method, identString))
					return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean permissionGranted(String actionPermission) {
		try (CloseableWrappedIterable<UserGroupPermission> cPermissions = permissions.getWrappedIterable()) {
			for (UserGroupPermission permission : cPermissions) {
				if (permission.permissionGranted(actionPermission))
					return true;
			}
		} catch (IOException e) {
			throw new UnexpectedException(e);
		}
		return false;
	}
}