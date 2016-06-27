package io.shockah.skylark.permissions.db;

import io.shockah.skylark.UnexpectedException;
import io.shockah.skylark.db.DbObject;
import io.shockah.skylark.ident.IdentMethod;
import java.io.IOException;
import com.j256.ormlite.dao.CloseableWrappedIterable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_usergroups")
public class UserGroup extends DbObject<UserGroup, Integer> {
	@DatabaseField(canBeNull = false)
	public String name;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupIdent> idents;
	
	@ForeignCollectionField(foreignFieldName = "userGroup")
	private ForeignCollection<UserGroupPermission> permissions;
	
	@Deprecated //ORMLite-only
	protected UserGroup() {
		super();
	}
	
	public UserGroup(Dao<UserGroup, Integer> dao) {
		super(dao);
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