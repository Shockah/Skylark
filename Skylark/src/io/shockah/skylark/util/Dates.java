package io.shockah.skylark.util;

import java.util.Date;

public final class Dates {
	public static Date inFuture(long time) {
		return new Date(new Date().getTime() + time);
	}
	
	public static Date inPast(long time) {
		return new Date(new Date().getTime() - time);
	}
	
	public static boolean isInFuture(Date date) {
		return new Date().before(date);
	}
	
	public static boolean isInPast(Date date) {
		return new Date().after(date);
	}
	
	private Dates() { }
}