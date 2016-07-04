package io.shockah.skylark.db;

import java.sql.SQLException;

@FunctionalInterface
public interface SQLExceptionWrappedAction2<A, B> {
	public void call(A t1, B t2) throws SQLException;
}