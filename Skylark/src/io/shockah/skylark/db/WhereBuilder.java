package io.shockah.skylark.db;

import java.lang.reflect.Field;
import java.sql.SQLException;
import java.util.regex.Pattern;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.stmt.SelectArg;
import com.j256.ormlite.stmt.Where;
import io.shockah.skylark.UnexpectedException;

public final class WhereBuilder {
	public static enum Operator {
		AND, OR;
	}
	
	public final Where<?, ?> where;
	public final Operator operator;
	private boolean first = true;
	
	public WhereBuilder(Where<?, ?> where) {
		this(where, Operator.AND);
	}
	
	public WhereBuilder(Where<?, ?> where, Operator operator) {
		this.where = where;
		this.operator = operator;
	}
	
	private void checkFirst() {
		if (first) {
			first = false;
		} else {
			switch (operator) {
				case AND:
					where.and();
					break;
				case OR:
					where.or();
					break;
			}
		}
	}
	
	private String getColumnName(Field field) {
		DatabaseField dbField = field.getAnnotation(DatabaseField.class);
		if (dbField == null)
			throw new IllegalArgumentException();
		if (dbField.columnName() == null)
			return field.getName();
		return dbField.columnName();
	}
	
	public WhereBuilder equals(Field field, Object value) {
		return equals(getColumnName(field), value);
	}
	
	public WhereBuilder equals(String columnName, Object value) {
		try {
			checkFirst();
			if (value == null)
				where.isNull(columnName);
			else
				where.eq(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder notEquals(Field field, Object value) {
		return notEquals(getColumnName(field), value);
	}
	
	public WhereBuilder notEquals(String columnName, Object value) {
		try {
			checkFirst();
			if (value == null)
				where.isNotNull(columnName);
			else
				where.ne(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder greater(Field field, Object value) {
		return greater(getColumnName(field), value);
	}
	
	public WhereBuilder greater(String columnName, Object value) {
		try {
			checkFirst();
			where.gt(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder greaterOrEqual(Field field, Object value) {
		return greaterOrEqual(getColumnName(field), value);
	}
	
	public WhereBuilder greaterOrEqual(String columnName, Object value) {
		try {
			checkFirst();
			where.ge(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder less(Field field, Object value) {
		return less(getColumnName(field), value);
	}
	
	public WhereBuilder less(String columnName, Object value) {
		try {
			checkFirst();
			where.lt(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder lessOrEqual(Field field, Object value) {
		return lessOrEqual(getColumnName(field), value);
	}
	
	public WhereBuilder lessOrEqual(String columnName, Object value) {
		try {
			checkFirst();
			where.le(columnName, value);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder between(Field field, Object low, Object high) {
		return between(getColumnName(field), low, high);
	}
	
	public WhereBuilder between(String columnName, Object low, Object high) {
		try {
			checkFirst();
			where.between(columnName, low, high);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder regexp(Field field, String pattern) {
		return regexp(getColumnName(field), pattern);
	}
	
	public WhereBuilder regexp(String columnName, String pattern) {
		try {
			checkFirst();
			where.rawComparison(columnName, "REGEXP", pattern);
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder regexp(Field field, Pattern pattern) {
		return regexp(getColumnName(field), pattern.pattern());
	}
	
	public WhereBuilder regexp(String columnName, Pattern pattern) {
		try {
			checkFirst();
			where.rawComparison(columnName, "REGEXP", pattern.pattern());
		} catch (SQLException e) {
			throw new UnexpectedException(e);
		}
		return this;
	}
	
	public WhereBuilder reverseRegexp(Field field, String value) {
		return reverseRegexp(getColumnName(field), value);
	}
	
	public WhereBuilder reverseRegexp(String columnName, String value) {
		checkFirst();
		where.raw(String.format("? REGEXP %s", columnName), new SelectArg(SqlType.STRING, value));
		return this;
	}
}