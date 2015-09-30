package pl.shockah;

import java.util.Arrays;
import java.util.List;

public final class Util {
	public static Class<?> getCallingClass(Class<?>... ignore) {
		List<Class<?>> list = Arrays.asList(ignore);
		StackTraceElement[] stes = new Throwable().getStackTrace();
		for (int i = 1; i < stes.length; i++) {
			try {
				Class<?> cls = Class.forName(stes[i].getClassName());
				if (!list.contains(cls)) return cls;
			} catch (Exception e) {e.printStackTrace();}
		}
		return null;
	}
	
	public static void sleep(long ms) {
		try {
			Thread.sleep(ms);
		} catch (Exception e) {}
	}
	
	public static boolean equals(Object o1, Object o2) {
		if (o1 == null && o2 == null) return true;
		if (o1 == null) return o2.equals(o1);
		return o1.equals(o2);
	}
}