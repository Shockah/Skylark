package io.shockah.skylark.db;

import java.sql.SQLException;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;
import io.shockah.json.JSONObject;
import io.shockah.json.JSONParser;
import io.shockah.json.JSONPrinter;

public class JSONObjectPersister extends BaseDataType {
	public JSONObjectPersister() {
		super(SqlType.LONG_STRING, new Class<?>[] { JSONObject.class });
	}

	@Override
	public Object parseDefaultString(FieldType fieldType, String defaultStr) throws SQLException {
		return defaultStr;
	}

	@Override
	public Object resultToSqlArg(FieldType fieldType, DatabaseResults results, int columnPos) throws SQLException {
		return results.getString(columnPos);
	}
	
	@Override
	public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
		if (fieldType == null)
			return sqlArg;
		return new JSONParser().parseObject((String)sqlArg);
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return new JSONPrinter().toString((JSONObject)javaObject);
	}
}