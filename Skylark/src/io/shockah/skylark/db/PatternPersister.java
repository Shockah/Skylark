package io.shockah.skylark.db;

import java.sql.SQLException;
import java.util.regex.Pattern;
import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.BaseDataType;
import com.j256.ormlite.support.DatabaseResults;

public class PatternPersister extends BaseDataType {
	public PatternPersister() {
		super(SqlType.STRING, new Class<?>[] { Pattern.class });
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
		return Pattern.compile((String)sqlArg);
	}
	
	@Override
	public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {
		return ((Pattern)javaObject).pattern();
	}
}