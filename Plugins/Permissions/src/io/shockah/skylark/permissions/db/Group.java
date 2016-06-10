package io.shockah.skylark.permissions.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = "io_shockah_skylark_permissions_groups")
public class Group {
	@DatabaseField(id = true)
	private String name;
	
	public Group() {
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
}