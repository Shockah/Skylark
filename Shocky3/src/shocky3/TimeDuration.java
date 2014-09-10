package shocky3;

import java.util.Date;

public final class TimeDuration {
	public static String format(Date date) {
		return format(date, new Date());
	}
	public static String format(Date date1, Date date2) {
		return formatMiliseconds(date2.getTime() - date1.getTime());
	}
	
	public static String formatSeconds(int s) {
		if (s <= 0) return "0s";
		int m = s / 60; s %= 60;
		int h = m / 60; m %= 60;
		int d = h / 24; h %= 24;
		int w = d / 7; d %= 7;
		
		StringBuilder sb = new StringBuilder();
		if (w != 0) sb.append(String.format(" %dw", w));
		if (w + d != 0) sb.append(String.format(" %dd", d));
		if (w + d + h != 0) sb.append(String.format(" %dh", h));
		if (w + d + h + m != 0) sb.append(String.format(" %dm", m));
		if (w + d + h + m + s != 0) sb.append(String.format(" %ds", s));
		return sb.toString().substring(1);
	}
	public static String formatMiliseconds(long ms) {
		return formatSeconds((int)(ms / 1000l));
	}
	
	private TimeDuration() {}
}