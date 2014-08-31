package shocky3;

import java.util.Date;

public final class TimeDuration {
	public static String format(Date date) {
		return format(date, new Date());
	}
	public static String format(Date date1, Date date2) {
		long diff = Math.abs(date2.getTime() - date1.getTime());
		
		int s = (int)(diff / 1000l);
		int m = s / 60; s %= 60;
		int h = m / 60; m %= 60;
		int d = h / 24; h %= 24;
		int w = d / 7; h %= 7;
		
		StringBuilder sb = new StringBuilder();
		if (w != 0) sb.append(String.format(" %dw", w));
		if (w + d != 0) sb.append(String.format(" %dd", d));
		if (w + d + h != 0) sb.append(String.format(" %dh", h));
		if (w + d + h + m != 0) sb.append(String.format(" %dm", m));
		if (w + d + h + m + s != 0) sb.append(String.format(" %ds", s));
		return sb.toString().substring(1);
	}
	
	private TimeDuration() {}
}