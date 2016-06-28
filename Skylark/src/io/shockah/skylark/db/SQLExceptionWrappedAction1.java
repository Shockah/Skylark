package io.shockah.skylark.db;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLExceptionWrappedAction1<A> {
	public void call(A t1) throws SQLException;
}